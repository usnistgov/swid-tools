package gov.nist.decima.swid;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSchemaFactory;
import org.xml.sax.SAXException;

import gov.nist.decima.assessment.AssessmentException;
import gov.nist.decima.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.assessment.result.AssessmentResults;
import gov.nist.decima.assessment.result.ResultWriter;
import gov.nist.decima.assessment.schema.AssessmentSAXErrorHandler;
import gov.nist.decima.assessment.schema.AssessmentXMLFilter;
import gov.nist.decima.assessment.schematron.SchematronAssessment;
import gov.nist.decima.document.DefaultXMLDocumentFactory;
import gov.nist.decima.document.XMLDocument;
import gov.nist.decima.document.XMLDocumentException;
import gov.nist.decima.module.cli.CLIParser;
import gov.nist.decima.module.cli.commons.cli.EnumerationOptionValidator;
import gov.nist.decima.requirement.DefaultRequirementsManager;
import gov.nist.decima.requirement.MutableRequirementsManager;
import gov.nist.decima.requirement.RequirementsParser;
import gov.nist.decima.schematron.DefaultSchematronCompiler;
import gov.nist.decima.schematron.Schematron;
import gov.nist.decima.schematron.SchematronCompilationException;
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
	

	public static void main(String[] args) throws SchematronCompilationException, XMLDocumentException, AssessmentException, JDOMException, SAXException, ParserException, URISyntaxException, IOException {
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

	protected String parseUseCase(String useCase, boolean authoritative) {
		return "swid."+useCase+"."+ (authoritative ? "auth" : "non-auth");
	}

	private int run(String[] args) throws ParseException, SchematronCompilationException, XMLDocumentException, AssessmentException, JDOMException, SAXException, ParserException, URISyntaxException, IOException {
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

		for (String path : paths) {
			AssessmentResults validationResult = validate(new File(path), tagType, authoritative);

			// Output the results
			ResultWriter writer = new ResultWriter();
			writer.write(validationResult, System.out);
//			Collection<BaseRequirementResult> results = validationResult.getBaseRequirementResults();
//			for (BaseRequirementResult reqResult : results) {
//				System.out.println(reqResult.getBaseRequirement().getId() + ": status=" + reqResult.getStatus());
//				for (DerivedRequirementResult derResult : reqResult.getDerivedRequirementResults()) {
//					System.out.println("  "+ derResult.getDerivedRequirement().getId() + ": status=" + derResult.getStatus());
//					for (TestResult asrResult : derResult.getTestResults()) {
//						System.out.println("    status=" + asrResult.getStatus() + ", message=" + asrResult.getResultValues()+", location="+asrResult.getContext().getLine()+","+asrResult.getContext().getColumn()+", xpath="+asrResult.getContext().getXPath());
//					}
//				}
//			}

		}

		return 0;
	}

	private AssessmentResults validate(File file, TagType tagType, boolean authoritative) throws XMLDocumentException, SchematronCompilationException, AssessmentException, JDOMException, SAXException, ParserException, URISyntaxException, IOException {

		// Load the document to assess
		DefaultXMLDocumentFactory documentFactory = new DefaultXMLDocumentFactory();
		XMLDocument doc = documentFactory.load(file);

		AssessmentResultBuilder builder = new AssessmentResultBuilder(new SWIDValResultStatusBehavior(tagType, authoritative));

		validateSchema(doc, builder);
		validateSchematron(doc, tagType, authoritative, builder);
		builder.end();

		// Generate the assessment results
//		RequirementsManager requirementsManager = new StubRequirementsManager(builder.getTestedDerivedRequirements());
		MutableRequirementsManager requirementsManager = new DefaultRequirementsManager();
		RequirementsParser parser = new RequirementsParser(Collections.singletonList(new StreamSource("classpath:swid-requirements-ext.xsd")));
		parser.parse(new URL("classpath:requirements.xml"), requirementsManager);
		return builder.build(requirementsManager);
	}

	private void validateSchema(XMLDocument document, AssessmentResultBuilder builder) throws SAXException, JDOMException, IOException {
		SchemaFactory schemafac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemafac.newSchema(new StreamSource("classpath:swid-schema-20151006.xsd"));
		XMLReaderJDOMFactory factory = new XMLReaderSchemaFactory(schema);
		SAXBuilder saxBuilder = new SAXBuilder(factory);

		AssessmentXMLFilter filter = new AssessmentXMLFilter();
		AssessmentSAXErrorHandler receiver = new AssessmentSAXErrorHandler(document, "GEN-1-1", builder, filter);
		saxBuilder.setErrorHandler(receiver);
		saxBuilder.setXMLFilter(filter);
		
		saxBuilder.build(document.getInputStream(), document.getSystemId());
	}

	private void validateSchematron(XMLDocument document, TagType tagType, boolean authoritative, AssessmentResultBuilder builder) throws SchematronCompilationException, MalformedURLException, AssessmentException {

		String phase = parseUseCase(tagType.getName(), authoritative);

		// Load the schematron
		Schematron schematron =  new DefaultSchematronCompiler().newSchematron(new URL("classpath:schematron/swid-nistir-8060.sch"));

		// Create the assessment
		SchematronAssessment assessment = new SchematronAssessment(schematron, phase);
		assessment.addParameter("authoritative", Boolean.toString(authoritative));
		assessment.addParameter("type", tagType.getName());
		File resultDir = new File("svrl-result");
		assessment.setResultDirectory(resultDir);
//		resultDir.mkdirs();

		// Perform the assessment
		assessment.execute(document, builder);
	}

}
