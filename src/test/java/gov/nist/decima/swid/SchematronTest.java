package gov.nist.decima.swid;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

import gov.nist.decima.assessment.AssessmentException;
import gov.nist.decima.assessment.BasicAssessmentExecutor;
import gov.nist.decima.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.assessment.result.AssessmentResults;
import gov.nist.decima.assessment.result.BaseRequirementResult;
import gov.nist.decima.assessment.result.DerivedRequirementResult;
import gov.nist.decima.assessment.result.TestResult;
import gov.nist.decima.assessment.schematron.SchematronAssessment;
import gov.nist.decima.document.DefaultXMLDocumentFactory;
import gov.nist.decima.document.XMLDocument;
import gov.nist.decima.document.XMLDocumentException;
import gov.nist.decima.requirement.RequirementsManager;
import gov.nist.decima.schematron.DefaultSchematronCompiler;
import gov.nist.decima.schematron.Schematron;
import gov.nist.decima.schematron.SchematronCompilationException;
import gov.nist.decima.testing.StubRequirementsManager;

public class SchematronTest {

	@Test
	public void temporaryTest() throws SchematronCompilationException, MalformedURLException, IOException, AssessmentException, XMLDocumentException {
		// Load the document to assess
		DefaultXMLDocumentFactory documentFactory = new DefaultXMLDocumentFactory();
		XMLDocument doc = documentFactory.load(new URL("classpath:templates/primary-auth-swid.xml"));

		// Load the schematron
		Schematron schematron =  new DefaultSchematronCompiler().newSchematron(new URL("classpath:schematron/swid-nistir-8060.sch"));

		// Create the assessment
		SchematronAssessment assessment = new SchematronAssessment(schematron, "swid.primary.auth");
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
		AssessmentResults validationResult = builder.build(requirementsManager);

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

	@Test
	@Ignore
	public void finalTest() throws SchematronCompilationException, MalformedURLException, IOException, AssessmentException, XMLDocumentException {
		// Load the document to assess
		DefaultXMLDocumentFactory documentFactory = new DefaultXMLDocumentFactory();
		XMLDocument documentToAssess = documentFactory.load(new URL("classpath:templates/primary-swid.xml"));

		// Create the assessment
		Schematron schematron = new DefaultSchematronCompiler().newSchematron(new URL("classpath:schematron/swid-nistir-8060.sch"));
		SchematronAssessment assessment = new SchematronAssessment(schematron, null);

		// Establish the requirements
		// TODO: Update this with a method to get an actual requirements manager instance
		RequirementsManager requirementsManager = new StubRequirementsManager(Collections.emptySet());

		// Perform the assessment
		BasicAssessmentExecutor executor = new BasicAssessmentExecutor(requirementsManager, Collections.singletonList(assessment));
		AssessmentResults validationResult = executor.execute(documentToAssess);

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
}
