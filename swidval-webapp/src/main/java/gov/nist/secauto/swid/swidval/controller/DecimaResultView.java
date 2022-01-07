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

package gov.nist.secauto.swid.swidval.controller;

import gov.nist.secauto.decima.core.assessment.result.AssessmentResults;
import gov.nist.secauto.decima.xml.assessment.result.ReportGenerator;
import gov.nist.secauto.decima.xml.assessment.result.XMLResultBuilder;

import org.jdom2.Document;
import org.jdom2.transform.JDOMSource;
import org.springframework.web.servlet.view.AbstractView;

import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

public class DecimaResultView
    extends AbstractView {

  @Override
  protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    AssessmentResults results = (AssessmentResults) model.get(SWIDValController.MODEL_KEY_ASSESSMENT_RESULT);

    XMLResultBuilder writer = new XMLResultBuilder();
    Document document = writer.newDocument(results);
    ReportGenerator reportGenerator = new ReportGenerator();
    reportGenerator.setIgnoreNotTestedResults(true);
    reportGenerator.setIgnoreOutOfScopeResults(true);
    reportGenerator.setXslTemplateExtension(new URI("classpath:xsl/swid-result.xsl"));
    // reportGenerator.setTargetName(model.get(SWIDValController.MODEL_KEY_FILENAME).toString());
    reportGenerator.generate(new JDOMSource(document), new StreamResult(response.getOutputStream()));
  }

}
