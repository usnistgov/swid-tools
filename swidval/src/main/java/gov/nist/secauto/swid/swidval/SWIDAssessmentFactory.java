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

import gov.nist.secauto.decima.core.assessment.Assessment;
import gov.nist.secauto.decima.core.assessment.AssessmentExecutor;
import gov.nist.secauto.decima.core.assessment.ConcurrentAssessmentExecutor;
import gov.nist.secauto.decima.core.classpath.ClasspathHandler;
import gov.nist.secauto.decima.xml.assessment.Factory;
import gov.nist.secauto.decima.xml.assessment.schema.SchemaAssessment;
import gov.nist.secauto.decima.xml.assessment.schematron.SchematronAssessment;
import gov.nist.secauto.decima.xml.document.XMLDocument;
import gov.nist.secauto.decima.xml.schematron.DefaultSchematronCompiler;
import gov.nist.secauto.decima.xml.schematron.Schematron;
import gov.nist.secauto.decima.xml.schematron.SchematronCompilationException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.xml.transform.stream.StreamSource;

public class SWIDAssessmentFactory {
  private static final SWIDAssessmentFactory INSTANCE;

  public static String toPhase(TagType tagType, boolean authoritative) {
    return "swid." + tagType.getName() + "." + (authoritative ? "auth" : "non-auth");
  }

  public static SWIDAssessmentFactory getInstance() {
    return INSTANCE;
  }

  static {
    INSTANCE = new SWIDAssessmentFactory();
  }

  private final Schematron schematron;
  private final SchemaAssessment schemaAssessment;
  private File resultDirectory;

  private SWIDAssessmentFactory() {
    ClasspathHandler.initialize();
    this.schematron = createSchematron();
    this.schemaAssessment = createSchemaAssessment();
  }

  public Schematron getSchematron() {
    return schematron;
  }

  /**
   * Retrieve the result directory.
   * 
   * @return the resultDirectory or <code>null</code> if no directory is set
   */
  public File getResultDirectory() {
    return resultDirectory;
  }

  /**
   * Set the result directory to use.
   * 
   * @param resultDirectory
   *          the resultDirectory to set
   */
  public void setResultDirectory(File resultDirectory) {
    this.resultDirectory = resultDirectory;
  }

  /**
   * Produces a new assessment executor for assessing a SWID tag.
   * 
   * @param tagType
   *          the software type supported by the tag
   * @param authoritative
   *          <code>true</code> if the tag was expected to be created by the software provider
   * @param executorService
   *          the Java executor to use to run the assessments
   * @return a new executor
   */
  public AssessmentExecutor<XMLDocument> newAssessmentExecutor(TagType tagType, boolean authoritative,
      ExecutorService executorService) {

    List<Assessment<XMLDocument>> assessments = new ArrayList<Assessment<XMLDocument>>(2);
    assessments.add(schemaAssessment);

    SchematronAssessment assessment = Factory.newSchematronAssessment(schematron, toPhase(tagType, authoritative));
    assessment.addParameter("authoritative", Boolean.toString(authoritative));
    assessment.addParameter("type", tagType.getName());
    assessments.add(assessment);
    if (resultDirectory != null) {
      resultDirectory.mkdirs();
      assessment.setResultDirectory(resultDirectory);
    }

    AssessmentExecutor<XMLDocument> executor
        = new ConcurrentAssessmentExecutor<XMLDocument>(executorService, assessments);
    return executor;
  }

  protected SchemaAssessment createSchemaAssessment() {
    return Factory.newSchemaAssessment("GEN-1-1",
        Collections.singletonList(new StreamSource("classpath:schema/swid-schema-fixed-20160908.xsd")));
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
