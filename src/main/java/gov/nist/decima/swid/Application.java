package gov.nist.decima.swid;

import static gov.nist.decima.module.cli.CLIParser.DEFAULT_VALIDATION_REPORT_FILE;
import static gov.nist.decima.module.cli.CLIParser.DEFAULT_VALIDATION_RESULT_FILE;
import static gov.nist.decima.module.cli.CLIParser.OPTION_VALIDATION_REPORT_FILE;
import static gov.nist.decima.module.cli.CLIParser.OPTION_VALIDATION_RESULT_FILE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.assessment.result.DefaultLoggingHandler;
import gov.nist.decima.core.assessment.result.LoggingHandler;
import gov.nist.decima.core.assessment.result.ReportGenerator;
import gov.nist.decima.core.assessment.result.XMLResultBuilder;
import gov.nist.decima.core.document.JDOMDocument;
import gov.nist.decima.core.document.XMLDocument;
import gov.nist.decima.core.document.XMLDocumentException;
import gov.nist.decima.core.requirement.RequirementsManager;
import gov.nist.decima.core.requirement.RequirementsParserException;
import gov.nist.decima.core.schematron.SchematronCompilationException;
import gov.nist.decima.module.cli.CLIParser;
import gov.nist.decima.module.cli.commons.cli.EnumerationOptionValidator;

public class Application {
	private static final Logger log = LogManager.getLogger(Application.class);
	private static final String OPTION_USECASE = "usecase";
	private static final String OPTION_USECASE_VALUE_PRIMARY = "primary";
	private static final String OPTION_USECASE_VALUE_CORPUS = "corpus";
	private static final String OPTION_USECASE_VALUE_PATCH = "patch";
	private static final String OPTION_USECASE_VALUE_SUPPLEMENTAL = "supplemental";
	private static final String OPTION_USECASE_DEFAULT_VALUE = OPTION_USECASE_VALUE_PRIMARY;

	private static final String OPTION_AUTHORITATIVE = "A";
	private static final String OPTION_NON_AUTHORITATIVE = "a";

	public static void main(String[] args) throws SchematronCompilationException, XMLDocumentException, AssessmentException, JDOMException, SAXException, URISyntaxException, IOException, RequirementsParserException, TransformerException {
		try {
			System.exit(new Application().run(args));
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}

	protected CommandLine parseCLI(String[] args) throws ParseException {
		CLIParser cliParser = new CLIParser("java -jar <decima jar> (options) <swid tag path>");
		cliParser.setVersion(Version.VERSION);

		Option useCase = Option.builder(OPTION_USECASE).desc("the SWID tag type, which is one of: primary, corpus, patch, or supplemental (default: primary)").hasArg().build();
		EnumerationOptionValidator useCaseValidator = new EnumerationOptionValidator(useCase);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_PRIMARY);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_CORPUS);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_PATCH);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_SUPPLEMENTAL);
		cliParser.addOption(useCaseValidator);

		OptionGroup authGroup = new OptionGroup();
		authGroup.addOption(Option.builder(OPTION_AUTHORITATIVE).desc("the tag is produced by an authoritative creator (default)").build());
		authGroup.addOption(Option.builder(OPTION_NON_AUTHORITATIVE).desc("the tag is not produced by an authoritative creator").build());
		cliParser.addOptionGroup(authGroup);

		return cliParser.parse(args);
	}

	private int run(String[] args) throws SchematronCompilationException, XMLDocumentException, AssessmentException, JDOMException, SAXException, URISyntaxException, IOException, RequirementsParserException, ParseException, TransformerException {
		CommandLine cmd = parseCLI(args);

		if (cmd == null) {
			return -1;
		}

		List<String> paths = cmd.getArgList();
		if (paths.isEmpty()) {
			System.err.println("At least one path must be specified as an extra argument.");
			return -2;
		} else if (paths.size() > 1) {
			System.err.println("Only one path must be specified as an extra argument.");
			return -3;
		}
		String path = paths.get(0);

		String tagTypeName = cmd.getOptionValue(OPTION_USECASE, OPTION_USECASE_DEFAULT_VALUE);

		TagType tagType = TagType.valueOf(tagTypeName.toUpperCase());
		boolean authoritative = !cmd.hasOption(OPTION_NON_AUTHORITATIVE);

		log.info("Validating tag: "+path);
		log.info("  tag type: "+tagType.getName());
		log.info("  authoritative tag: "+authoritative);

//		 handle resultfile
		File validationResultFile;
		{
			String fileValue = cmd.getOptionValue(OPTION_VALIDATION_RESULT_FILE, DEFAULT_VALIDATION_RESULT_FILE);
			validationResultFile = new File(fileValue);
			File parentDir = validationResultFile.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}
		}

		// handle reportfile
		File validationReportFile;
		{
			String fileValue = cmd.getOptionValue(OPTION_VALIDATION_REPORT_FILE, DEFAULT_VALIDATION_REPORT_FILE);
			validationReportFile = new File(fileValue);
			File parentDir = validationReportFile.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				parentDir.mkdirs();
			}
		}

		// Load the document to assess
		File file = new File(path);
		XMLDocument doc = new JDOMDocument(file);

		ExecutorService executorService = null;
		AssessmentResults validationResult;
		try {
			executorService = Executors.newFixedThreadPool(2);
	
			// Configure the assessments
			RequirementsManager requirementsManager = SWIDAssessmentFactory.getInstance().getRequirementsManager();
			LoggingHandler loggingHandler = new DefaultLoggingHandler(requirementsManager);
			AssessmentExecutor executor = SWIDAssessmentFactory.getInstance().newAssessmentExecutor(tagType, authoritative, executorService, loggingHandler);
			validationResult = executor.execute(doc);
		} finally {
			if (executorService != null) {
				executorService.shutdown();
			}
		}

		// Output the results
		XMLResultBuilder writer = new XMLResultBuilder();
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(validationResultFile))) {
			log.info("Storing assessment results to: "+validationResultFile);
			writer.write(validationResult, os);
		}

		// output the report
		ReportGenerator reportGenerator = new ReportGenerator();
		// This is the location of the bootstrap directory, which is intended to be relative to the install location of SWIDVal
		reportGenerator.setBootstrapDir(new File("bootstrap"));
		reportGenerator.setIgnoreNotTestedResults(true);
		reportGenerator.setIgnoreOutOfScopeResults(true);
		reportGenerator.setXslTemplateExtension(new URI("classpath:xsl/swid-result.xsl"));
		log.info("Generating HTML report to: "+validationReportFile);
		reportGenerator.generate(validationResultFile, validationReportFile);
		return 0;
	}
}
