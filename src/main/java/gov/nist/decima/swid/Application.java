package gov.nist.decima.swid;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import gov.nist.decima.core.assessment.Assessment;
import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.ConcurrentAssessmentExecutor;
import gov.nist.decima.core.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.assessment.result.ResultWriter;
import gov.nist.decima.core.assessment.schema.SchemaAssessment;
import gov.nist.decima.core.assessment.schematron.SchematronAssessment;
import gov.nist.decima.core.document.JDOMDocument;
import gov.nist.decima.core.document.XMLDocument;
import gov.nist.decima.core.document.XMLDocumentException;
import gov.nist.decima.core.requirement.DefaultRequirementsManager;
import gov.nist.decima.core.requirement.MutableRequirementsManager;
import gov.nist.decima.core.requirement.RequirementsParser;
import gov.nist.decima.core.requirement.RequirementsParserException;
import gov.nist.decima.core.schematron.SchematronCompilationException;
import gov.nist.decima.module.cli.CLIParser;
import gov.nist.decima.module.cli.commons.cli.EnumerationOptionValidator;
import gov.nist.decima.testing.ParserException;

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
	

	public static void main(String[] args) throws SchematronCompilationException, XMLDocumentException, AssessmentException, JDOMException, SAXException, ParserException, URISyntaxException, IOException, RequirementsParserException {
		try {
			System.exit(new Application().run(args));
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-2);
		}
	}

	protected CommandLine parseCLI(String[] args) throws ParseException {
		CLIParser cliParser = new CLIParser("java -jar <decima jar> (options) <path-1> ... <path-n>");

		Option useCase = Option.builder(OPTION_USECASE).desc("the SWID tag type (default: primary)").hasArg().build();
		EnumerationOptionValidator useCaseValidator = new EnumerationOptionValidator(useCase);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_PRIMARY);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_CORPUS);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_PATCH);
		useCaseValidator.addAllowedValue(OPTION_USECASE_VALUE_SUPPLEMENTAL);
		cliParser.addOption(useCaseValidator);

		OptionGroup authGroup = new OptionGroup();
		authGroup.addOption(Option.builder(OPTION_AUTHORITATIVE).desc("the tag is produced by an authoritative creator (default)").build());
		authGroup.addOption(Option.builder(OPTION_NON_AUTHORITATIVE).desc("the tag is not produced by an authoritative creator (default)").build());
		cliParser.addOptionGroup(authGroup);

		return cliParser.parse(args);
	}

	private int run(String[] args) throws SchematronCompilationException, XMLDocumentException, AssessmentException, JDOMException, SAXException, ParserException, URISyntaxException, IOException, RequirementsParserException, ParseException {
		CommandLine cmd = parseCLI(args);

		List<String> paths = cmd.getArgList();
		if (paths.isEmpty()) {
			System.err.println("At least one path must be specified as an extra argument.");
			return -1;
		}
		String tagTypeName = cmd.getOptionValue(OPTION_USECASE, OPTION_USECASE_DEFAULT_VALUE);

		TagType tagType = TagType.valueOf(tagTypeName.toUpperCase());
		boolean authoritative = !cmd.hasOption(OPTION_NON_AUTHORITATIVE);

		log.info("Validating paths: "+paths);
		log.info("  tag type: "+tagType.getName());
		log.info("  authoritative tag: "+authoritative);

		// Load the requirements
		MutableRequirementsManager requirementsManager = new DefaultRequirementsManager();
		RequirementsParser parser = new RequirementsParser(Collections.singletonList(new StreamSource("classpath:swid-requirements-ext.xsd")));
		parser.parse(new URL("classpath:requirements.xml"), requirementsManager);

		ExecutorService executorService = Executors.newFixedThreadPool(2);
		
		// Configure the assessments
		AssessmentExecutor executor = configureAssessments(executorService, requirementsManager, tagType, authoritative);
		for (String path : paths) {
			// Load the document to assess
			XMLDocument doc = new JDOMDocument(new File(path));

			AssessmentResults validationResult = executor.execute(doc);

			// Output the results
			ResultWriter writer = new ResultWriter();
			writer.write(validationResult, System.out);
		}

		executorService.shutdown();

		return 0;
	}

	private AssessmentExecutor configureAssessments(ExecutorService executorService,
			MutableRequirementsManager requirementsManager, TagType tagType, boolean authoritative) throws AssessmentException, SchematronCompilationException, MalformedURLException {

		List<Assessment> assessments = new ArrayList<Assessment>(2);
		assessments.add(configureSchemaAssessment());
		assessments.add(configureSchematronAssessment(tagType, authoritative));

//		AssessmentExecutor executor = new BasicAssessmentExecutor(requirementsManager, assessments) {
		AssessmentExecutor retval = new ConcurrentAssessmentExecutor(executorService, requirementsManager, assessments) {
			@Override
			protected AssessmentResultBuilder newAssessmentResultBuilder() {
				return new AssessmentResultBuilder(new SWIDValResultStatusBehavior(tagType, authoritative));
			}
		};
		return retval;
	}

	private SchemaAssessment configureSchemaAssessment() throws AssessmentException {
		return new SchemaAssessment("GEN-1-1", Collections.singletonList(new StreamSource("classpath:swid-schema-20151006.xsd")));
	}

	private SchematronAssessment configureSchematronAssessment(TagType tagType, boolean authoritative) throws AssessmentException, SchematronCompilationException, MalformedURLException {

		String phase = createPhase(tagType.getName(), authoritative);

		// Load the Schematron and create the assessment
		SchematronAssessment assessment = new SchematronAssessment(new URL("classpath:schematron/swid-nistir-8060.sch"), phase);
		assessment.addParameter("authoritative", Boolean.toString(authoritative));
		assessment.addParameter("type", tagType.getName());
		assessment.setResultDirectory(new File("svrl-result"));
		return assessment;
	}

	protected String createPhase(String useCase, boolean authoritative) {
		return "swid."+useCase+"."+ (authoritative ? "auth" : "non-auth");
	}

}
