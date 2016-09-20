package gov.nist.decima.swid;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.xml.transform.stream.StreamSource;

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import gov.nist.decima.core.assessment.Assessment;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.ConcurrentAssessmentExecutor;
import gov.nist.decima.core.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.core.assessment.result.LoggingHandler;
import gov.nist.decima.core.assessment.schema.SchemaAssessment;
import gov.nist.decima.core.assessment.schematron.SchematronAssessment;
import gov.nist.decima.core.requirement.DefaultRequirementsManager;
import gov.nist.decima.core.requirement.MutableRequirementsManager;
import gov.nist.decima.core.requirement.RequirementsManager;
import gov.nist.decima.core.requirement.RequirementsParser;
import gov.nist.decima.core.requirement.RequirementsParserException;
import gov.nist.decima.core.schematron.DefaultSchematronCompiler;
import gov.nist.decima.core.schematron.Schematron;
import gov.nist.decima.core.schematron.SchematronCompilationException;

public class SWIDAssessmentFactory {
	private static final SWIDAssessmentFactory INSTANCE;
	protected static final String PROPERTY_KEY_AUTHORITATIVE = "authoritative";
	protected static final String PROPERTY_KEY_TAG_TYPE = "tag-type";
	private final Schematron schematron;
	private final SchemaAssessment schemaAssessment;
	private final RequirementsManager requirementsManager;

	public static String toPhase(TagType tagType, boolean authoritative) {
		return "swid."+tagType.getName()+"."+ (authoritative ? "auth" : "non-auth");
	}

	public static SWIDAssessmentFactory getInstance() {
		return INSTANCE;
	}

	static {
		INSTANCE = new SWIDAssessmentFactory();
	}

	private SWIDAssessmentFactory() {
		this.schematron = createSchematron();
		this.schemaAssessment = createSchemaAssessment();
		this.requirementsManager = loadRequirements();
	}

	public Schematron getSchematron() {
		return schematron;
	}

	public RequirementsManager getRequirementsManager() {
		return requirementsManager;
	}

	public AssessmentExecutor newAssessmentExecutor(TagType tagType, boolean authoritative, ExecutorService executorService) {
		return newAssessmentExecutor(tagType, authoritative, executorService, null);
	}

	public AssessmentExecutor newAssessmentExecutor(TagType tagType, boolean authoritative, ExecutorService executorService, LoggingHandler loggingHandler) {
		SchematronAssessment assessment = new SchematronAssessment(schematron, toPhase(tagType, authoritative));
		assessment.addParameter("authoritative", Boolean.toString(authoritative));
		assessment.addParameter("type", tagType.getName());

		List<Assessment> assessments = new ArrayList<Assessment>(2);
		assessments.add(schemaAssessment);
		assessments.add(assessment);

		AssessmentExecutor executor = new ConcurrentAssessmentExecutor(executorService, requirementsManager, assessments) {
			@Override
			protected AssessmentResultBuilder newAssessmentResultBuilder() {
				AssessmentResultBuilder retval = new AssessmentResultBuilder(new SWIDValResultStatusBehavior(tagType, true));
				if (loggingHandler != null) {
					retval.setLoggingHandler(loggingHandler);
				}
				retval.assignProperty(PROPERTY_KEY_AUTHORITATIVE, Boolean.toString(authoritative));
				retval.assignProperty(PROPERTY_KEY_TAG_TYPE, tagType.getName());
				return retval;
			}
		};
		return executor;
	}

	protected RequirementsManager loadRequirements() {
		MutableRequirementsManager requirementsManager = new DefaultRequirementsManager();
		try {
			RequirementsParser parser = new RequirementsParser(Collections.singletonList(new StreamSource("classpath:swid-requirements-ext.xsd")));
			parser.parse(new URL("classpath:requirements.xml"), requirementsManager);
		} catch (MalformedURLException | RequirementsParserException | URISyntaxException | JDOMException | SAXException e) {
			throw new RuntimeException(e);
		}
		return requirementsManager;
	}

	protected SchemaAssessment createSchemaAssessment() {
		return new SchemaAssessment("GEN-1-1", Collections.singletonList(new StreamSource("classpath:swid-schema-fixed-20160908.xsd")));
	}

	protected Schematron createSchematron() {
		URL schematronURL;
		try {
			schematronURL = new URL("classpath:schematron/swid-nistir-8060.sch");
			return new DefaultSchematronCompiler().newSchematron(schematronURL);
		} catch (MalformedURLException | SchematronCompilationException e) {
			// this should not happen if the classpath is resolvable and valid
			throw new RuntimeException(e);
		}
	}
}
