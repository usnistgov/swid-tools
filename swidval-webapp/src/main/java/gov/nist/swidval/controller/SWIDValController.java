
package gov.nist.swidval.controller;

import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.assessment.result.ResultStatus;
import gov.nist.decima.core.document.DocumentException;
import gov.nist.decima.swid.SWIDAssessmentReactor;
import gov.nist.decima.swid.TagType;
import gov.nist.decima.xml.assessment.result.ReportGenerator;
import gov.nist.decima.xml.assessment.result.XMLResultBuilder;
import gov.nist.decima.xml.assessment.schematron.SchematronAssessment;
import gov.nist.decima.xml.document.JDOMDocument;
import gov.nist.decima.xml.document.XMLDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.transform.JDOMSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

@RestController
@Controller
public class SWIDValController {
  private static final Logger log = LogManager.getLogger(SchematronAssessment.class);

  public static final String MODEL_KEY_ASSESSMENT_RESULT = "assessment";
  public static final String MODEL_KEY_FILENAME = "filename";

  private final SWIDAssessmentManager manager = new SWIDAssessmentManager();

  // @PostMapping("/validate.html")
  @RequestMapping(name = "/validate", method = RequestMethod.POST)
  public void validate(HttpServletRequest requestEntity, HttpServletResponse response)
      throws AssessmentException, UnrecognizedContentException, DocumentException, IOException, TransformerException, URISyntaxException {

    Enumeration<String> headers = requestEntity.getHeaderNames();
    InputStream is = requestEntity.getInputStream();
    // File tempFile = File.createTempFile("swid", ".swidtag");
    // tempFile.deleteOnExit();
    // file.transferTo(tempFile);

    // TagType type = TagType.lookup(tagType);
    TagType type = TagType.PRIMARY;
    AssessmentExecutor<XMLDocument> executor = manager.getAssessmentExecutor(type);
    SWIDAssessmentReactor reactor = new SWIDAssessmentReactor(type, false);
    reactor.pushAssessmentExecution(new JDOMDocument(is, null), executor);
    AssessmentResults results = reactor.react();

    String accept = requestEntity.getHeader("Accept");
    if (accept == null) {
      accept = "application/xml";
    }
    XMLResultBuilder writer = new XMLResultBuilder();
    if (accept.equals("application/xml")) {
      writer.write(results, response.getOutputStream());
    } else if (accept.equals("text/html")) {
      Document document = writer.newDocument(results);
      ReportGenerator reportGenerator = new ReportGenerator();
      reportGenerator.setIgnoreNotTestedResults(true);
      reportGenerator.setIgnoreOutOfScopeResults(true);
      reportGenerator.setXslTemplateExtension(new URI("classpath:xsl/swid-result.xsl"));
//      reportGenerator.setTargetName(model.get(SWIDValController.MODEL_KEY_FILENAME).toString());
      reportGenerator.generate(new JDOMSource(document), new StreamResult(response.getOutputStream()));
      
    }
  }

  @RequestMapping("/validate-form")
  public ModelAndView validateForm(@RequestParam("file") MultipartFile file, @RequestParam("tag-type") String tagType)
      throws AssessmentException, UnrecognizedContentException, DocumentException, IOException {
    if (file.isEmpty()) {
      throw new UnrecognizedContentException("A valid SWID tag was not provided.");
    }

    InputStream is = file.getInputStream();
    // File tempFile = File.createTempFile("swid", ".swidtag");
    // tempFile.deleteOnExit();
    // file.transferTo(tempFile);

    TagType type = TagType.lookup(tagType);
    AssessmentExecutor<XMLDocument> executor = manager.getAssessmentExecutor(type);
    SWIDAssessmentReactor reactor = new SWIDAssessmentReactor(type, false);
    reactor.pushAssessmentExecution(new JDOMDocument(is, null), executor);
    AssessmentResults results = reactor.react();

    if (!ResultStatus.PASS.equals(results.getBaseRequirementResult("GEN-1").getStatus())) {
      throw new UnrecognizedContentException("The provided file was not a schema valid SWID tag.");
    }

    Map<String, Object> model = new HashMap<>();
    model.put(MODEL_KEY_ASSESSMENT_RESULT, results);
    model.put(MODEL_KEY_FILENAME, file.getOriginalFilename());
    return new ModelAndView(new DecimaResultView(), model);
  }

  @ExceptionHandler(Exception.class)
  public void handleError(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
    log.error("Requested URL '" + request.getRequestURL() + "' raised an exception:", ex);

    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    // ModelAndView modelAndView = new ModelAndView();
    // modelAndView.addObject("exception", ex);
    // modelAndView.addObject("url", request.getRequestURL());
    //
    // modelAndView.setViewName("error");
    // return modelAndView;
  }
}
