package gov.nist.decima.swid;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gov.nist.decima.assessment.AssessmentException;
import gov.nist.decima.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.assessment.result.AssessmentResults;
import gov.nist.decima.assessment.result.BaseRequirementResult;
import gov.nist.decima.assessment.result.DerivedRequirementResult;
import gov.nist.decima.assessment.result.TestResult;
import gov.nist.decima.assessment.schematron.SchematronAssessment;
import gov.nist.decima.document.DefaultXMLDocumentFactory;
import gov.nist.decima.document.XMLDocument;
import gov.nist.decima.document.XMLDocumentException;
import gov.nist.decima.module.cli.CLIParser;
import gov.nist.decima.module.cli.commons.cli.EnumerationOptionValidator;
import gov.nist.decima.requirement.RequirementsManager;
import gov.nist.decima.schematron.DefaultSchematronCompiler;
import gov.nist.decima.schematron.Schematron;
import gov.nist.decima.schematron.SchematronCompilationException;
import gov.nist.decima.testing.StubRequirementsManager;

public class Application {
	private static final Logger log = LogManager.getLogger(Application.class);
	private static final String OPTION_USECASE = "usecase";
	private static final String OPTION_USECASE_DEFAULT_VALUE = "primary";

	public static void main(String[] args) throws FileNotFoundException, SchematronCompilationException, MalformedURLException, XMLDocumentException, AssessmentException {
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
		useCaseValidator.addAllowedValue("primary");
		useCaseValidator.addAllowedValue("corpus");
		useCaseValidator.addAllowedValue("patch");
		useCaseValidator.addAllowedValue("supplimental");
		cliParser.addOption(useCaseValidator);

		return cliParser.parse(args);
	}

	protected String parseUseCase(String useCase, boolean authoritative) {
		return "swid."+useCase+"."+ (authoritative ? "auth" : "non-auth");
	}

	private int run(String[] args) throws ParseException, FileNotFoundException, SchematronCompilationException, MalformedURLException, XMLDocumentException, AssessmentException {
		CommandLine cmd = parseCLI(args);

		List<String> paths = cmd.getArgList();
		if (paths.isEmpty()) {
			System.err.println("At least one path must be specified as an extra argument.");
			return -1;
		}
		String useCase = cmd.getOptionValue(OPTION_USECASE, OPTION_USECASE_DEFAULT_VALUE);
		String phase = parseUseCase(useCase, true);
		log.info("Validating paths: "+paths);
		log.info("  for use case: "+useCase);
		log.info("  using pahse: "+phase);

		for (String path : paths) {
			AssessmentResults validationResult = validate(new File(path), phase);

			// Output the results
			Collection<BaseRequirementResult> results = validationResult.getBaseRequirementResults();
			for (BaseRequirementResult reqResult : results) {
				System.out.println(reqResult.getBaseRequirement().getId() + ": status=" + reqResult.getStatus());
				for (DerivedRequirementResult derResult : reqResult.getDerivedRequirementResults()) {
					System.out.println("  "+ derResult.getDerivedRequirement().getId() + ": status=" + derResult.getStatus());
					for (TestResult asrResult : derResult.getTestResults()) {
						System.out.println("    status=" + asrResult.getStatus() + ", message=" + asrResult.getResultValues()+", location="+asrResult.getContext().getLine()+","+asrResult.getContext().getColumn()+", xpath="+asrResult.getContext().getXPath());
					}
				}
			}

		}

		return 0;
	}

	private AssessmentResults validate(File file, String phase) throws FileNotFoundException, XMLDocumentException, SchematronCompilationException, MalformedURLException, AssessmentException {
		// Load the document to assess
		DefaultXMLDocumentFactory documentFactory = new DefaultXMLDocumentFactory();
		XMLDocument doc = documentFactory.load(file);

		// Load the schematron
		Schematron schematron =  new DefaultSchematronCompiler().newSchematron(new URL("classpath:schematron/swid-nistir-8060.sch"));

		// Create the assessment
		SchematronAssessment assessment = new SchematronAssessment(schematron, phase);
		File resultDir = new File("svrl-result");
		assessment.setResultDirectory(resultDir);
//		resultDir.mkdirs();

		// Perform the assessment
		AssessmentResultBuilder builder = new AssessmentResultBuilder();
		assessment.execute(doc, builder);
		builder.end();

		// Generate the assessment results
		// TODO: replace this once the requirements manager is implemented
		RequirementsManager requirementsManager = new StubRequirementsManager(builder.getTestedDerivedRequirements());
		return builder.build(requirementsManager);
	}

}
