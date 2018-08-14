
package gov.nist.swidval.controller;

import java.net.URI;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.jdom2.Document;
import org.jdom2.transform.JDOMSource;
import org.springframework.web.servlet.view.AbstractView;

import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.xml.assessment.result.ReportGenerator;
import gov.nist.decima.xml.assessment.result.XMLResultBuilder;

public class DecimaResultView extends AbstractView {

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
