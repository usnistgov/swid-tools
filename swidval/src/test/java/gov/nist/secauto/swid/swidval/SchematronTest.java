/**
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.swid.swidval;

import gov.nist.secauto.decima.core.assessment.AssessmentException;
import gov.nist.secauto.decima.core.assessment.AssessmentExecutor;
import gov.nist.secauto.decima.core.assessment.result.AssessmentResultBuilder;
import gov.nist.secauto.decima.core.assessment.result.AssessmentResults;
import gov.nist.secauto.decima.core.assessment.result.BaseRequirementResult;
import gov.nist.secauto.decima.core.assessment.result.DerivedRequirementResult;
import gov.nist.secauto.decima.core.assessment.result.TestResult;
import gov.nist.secauto.decima.core.classpath.ClasspathHandler;
import gov.nist.secauto.decima.core.document.DocumentException;
import gov.nist.secauto.decima.xml.assessment.Factory;
import gov.nist.secauto.decima.xml.assessment.result.XPathContext;
import gov.nist.secauto.decima.xml.assessment.schematron.SchematronAssessment;
import gov.nist.secauto.decima.xml.document.DefaultXMLDocumentFactory;
import gov.nist.secauto.decima.xml.document.XMLDocument;
import gov.nist.secauto.decima.xml.schematron.DefaultSchematronCompiler;
import gov.nist.secauto.decima.xml.schematron.Schematron;
import gov.nist.secauto.decima.xml.schematron.SchematronCompilationException;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Executors;

public class SchematronTest {

  @BeforeClass
  public static void initialize() {
    ClasspathHandler.initialize();
  }

  @Test
  public void temporaryTest() throws SchematronCompilationException, MalformedURLException, IOException,
      AssessmentException, DocumentException {
    // Load the document to assess
    DefaultXMLDocumentFactory documentFactory = new DefaultXMLDocumentFactory();
    XMLDocument doc = documentFactory.load(new URL("classpath:templates/primary-auth-swid.xml"));

    // Load the schematron
    Schematron schematron
        = new DefaultSchematronCompiler().newSchematron(new URL("classpath:schematron/swid-nistir-8060.sch"));

    // Create the assessment
    SchematronAssessment assessment = Factory.newSchematronAssessment(schematron, "swid.primary.auth");
    File resultDir = new File("svrl-result");
    assessment.setResultDirectory(resultDir);
    // resultDir.mkdirs();

    // Perform the assessment
    SWIDAssessmentFactory factory = SWIDAssessmentFactory.getInstance();
    factory.setResultDirectory(new File("schematron-results"));

    AssessmentExecutor<XMLDocument> executor
        = factory.newAssessmentExecutor(TagType.PRIMARY, true, Executors.newSingleThreadExecutor());
    AssessmentResultBuilder assessmentResultBuilder
        = SWIDAssessmentResultBuilderFactory.newAssessmentResultBuilder(TagType.PRIMARY, true);
    executor.execute(doc, assessmentResultBuilder);

    // Generate the assessment results
    AssessmentResults validationResult = assessmentResultBuilder.end().build(SWIDRequirementsManager.getInstance());

    // Output the results
    Collection<BaseRequirementResult> results = validationResult.getBaseRequirementResults();
    for (BaseRequirementResult reqResult : results) {
      System.out.println(reqResult.getBaseRequirement().getId() + ": status=" + reqResult.getStatus());
      for (DerivedRequirementResult derResult : reqResult.getDerivedRequirementResults()) {
        System.out.println("  " + derResult.getDerivedRequirement().getId() + ": status=" + derResult.getStatus());
        for (TestResult asrResult : derResult.getTestResults()) {
          System.out.println("    status=" + asrResult.getStatus() + ", message=" + asrResult.getResultValues()
              + ", location=" + asrResult.getContext().getLine() + "," + asrResult.getContext().getColumn() + ", xpath="
              + ((XPathContext) asrResult.getContext()).getXPath());
        }
      }
    }
  }
}
