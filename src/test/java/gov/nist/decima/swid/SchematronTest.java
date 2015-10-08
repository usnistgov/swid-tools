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
import gov.nist.decima.assessment.AssessmentExecutor;
import gov.nist.decima.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.assessment.result.AssessmentResults;
import gov.nist.decima.assessment.result.BaseRequirementResult;
import gov.nist.decima.assessment.result.DerivedRequirementResult;
import gov.nist.decima.assessment.result.TestResult;
import gov.nist.decima.assessment.schematron.DefaultSchematronEvaluator;
import gov.nist.decima.assessment.schematron.SchematronAssessment;
import gov.nist.decima.assessment.schematron.SchematronEvaluator;
import gov.nist.decima.requirement.RequirementsManager;
import gov.nist.decima.schematron.DefaultSchematronCompiler;
import gov.nist.decima.schematron.Schematron;
import gov.nist.decima.schematron.SchematronCompilationException;
import gov.nist.decima.schematron.SchematronCompiler;
import gov.nist.decima.template.FileTemplate;
import gov.nist.decima.template.Template;
import gov.nist.decima.testing.StubRequirementsManager;

public class SchematronTest {

	@Test
	public void temporaryTest() throws SchematronCompilationException, MalformedURLException, IOException, AssessmentException {
		// TODO: replace this once the requirements manager is implemented
		SchematronCompiler schematronCompiler = new DefaultSchematronCompiler();
		Schematron schematron = schematronCompiler.newSchematron(new URL("classpath:schematron/swid-nistir-8060.sch"));
		SchematronEvaluator schematronEvaluator = new DefaultSchematronEvaluator(schematron);
		File resultDir = new File("svrl-result");
		SchematronAssessment assessment = new SchematronAssessment(schematronEvaluator);
		assessment.setResultDirectory(resultDir);
		Template doc = new FileTemplate(new URL("classpath:templates/primary-swid.xml"));
		AssessmentResultBuilder builder = new AssessmentResultBuilder();
		resultDir.mkdirs();
		assessment.execute(doc, builder);
		builder.end();
		
//
//		ResultGeneratingSVRLHandler svrlHandler = new ResultGeneratingSVRLHandler(builder, doc);
//		@SuppressWarnings("unused")
//		SchematronOutputDocument output = SVRLParser.parse(svrlHandler, domResult.getNode());


		RequirementsManager requirementsManager = new StubRequirementsManager(builder.getTestedDerivedRequirements());
		AssessmentResults validationResult = builder.build(requirementsManager);

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
	public void finalTest() throws SchematronCompilationException, MalformedURLException, IOException, AssessmentException {
		SchematronCompiler schematronCompiler = new DefaultSchematronCompiler();
		Schematron schematron = schematronCompiler.newSchematron(new URL("classpath:schematron/swid-nistir-8060.sch"));
		SchematronEvaluator schematronEvaluator = new DefaultSchematronEvaluator(schematron);
		SchematronAssessment assessment = new SchematronAssessment(schematronEvaluator);

		// TODO: Update this with a method to get an actual requirements manager instance
		RequirementsManager requirementsManager = new StubRequirementsManager(Collections.emptySet());
		AssessmentExecutor executor = new AssessmentExecutor(requirementsManager, Collections.singletonList(assessment));

		Template doc = new FileTemplate(new URL("classpath:templates/primary-swid.xml"));
		AssessmentResults validationResult = executor.execute(doc);

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
