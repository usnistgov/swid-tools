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
package gov.nist.swidval.controller;

import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.assessment.result.ResultStatus;
import gov.nist.decima.core.document.DocumentException;
import gov.nist.decima.swid.SWIDAssessmentResultBuilderFactory;
import gov.nist.decima.swid.SWIDRequirementsManager;
import gov.nist.decima.swid.SWIDTagCharacteristics;
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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

@RestController
@Controller
public class SWIDValController {
  private static final Logger log = LogManager.getLogger(SchematronAssessment.class);

  public static final String MODEL_KEY_ASSESSMENT_RESULT = "assessment";
  public static final String MODEL_KEY_FILENAME = "filename";

  private static final TagType TAG_TYPE_DEFAULT = TagType.PRIMARY;
  private static final boolean TAG_AUTHORITATIVE_DEFAULT = true;

  private final SWIDAssessmentManager manager = new SWIDAssessmentManager();

  // @PostMapping("/validate.html")
  @RequestMapping(name = "/validate", method = RequestMethod.POST)
  public void validate(HttpServletRequest requestEntity, HttpServletResponse response) throws AssessmentException,
      UnrecognizedContentException, DocumentException, IOException, TransformerException, URISyntaxException {

    InputStream is = requestEntity.getInputStream();
    // File tempFile = File.createTempFile("swid", ".swidtag");
    // tempFile.deleteOnExit();
    // file.transferTo(tempFile);

    JDOMDocument swidDocuemnt = new JDOMDocument(is, null);

    // TagType tagType = TagType.lookup(tagType);
    SWIDTagCharacteristics swidTagCharacteristics = getSWIDTagCharacteristics(swidDocuemnt);

    AssessmentResults results = performAssessment(swidDocuemnt, swidTagCharacteristics);

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
      // reportGenerator.setTargetName(model.get(SWIDValController.MODEL_KEY_FILENAME).toString());
      reportGenerator.generate(new JDOMSource(document), new StreamResult(response.getOutputStream()));

    }
  }

  @RequestMapping("/validate-form")
  public ModelAndView validateForm(@RequestParam("file") MultipartFile file,
      @RequestParam("tag-type") String tagTypeParam)
      throws AssessmentException, UnrecognizedContentException, DocumentException, IOException {
    if (file.isEmpty()) {
      throw new UnrecognizedContentException("A valid SWID tag was not provided.");
    }

    InputStream is = file.getInputStream();
    // File tempFile = File.createTempFile("swid", ".swidtag");
    // tempFile.deleteOnExit();
    // file.transferTo(tempFile);

    XMLDocument swidDocuemnt = new JDOMDocument(is, null);

    SWIDTagCharacteristics swidTagCharacteristics = getSWIDTagCharacteristics(swidDocuemnt);
    if (tagTypeParam != null) {
      TagType tagType = TagType.lookup(tagTypeParam);
      if (tagType != null) {
        // override with requested type
        swidTagCharacteristics = new SWIDTagCharacteristics(tagType, swidTagCharacteristics.isAuthoritative());
      }
    }

    AssessmentResults results = performAssessment(swidDocuemnt, swidTagCharacteristics);

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

  private SWIDTagCharacteristics getSWIDTagCharacteristics(XMLDocument swidDocuemnt) {
    SWIDTagCharacteristics characteristics;
    try {
      characteristics = SWIDTagCharacteristics.getSWIDTagCharacteristics(swidDocuemnt);
      log.debug("Based on the tag contents, the tag appears to be {} {} tag",
          characteristics.isAuthoritative() ? "an authoritative" : "a non-authoritative",
          characteristics.getTagType().getName());
    } catch (XPathExpressionException | XPathFactoryConfigurationException ex) {
      log.debug("Unable to determine the type and authoritativeness of the tag", ex);
      characteristics = new SWIDTagCharacteristics(TAG_TYPE_DEFAULT, TAG_AUTHORITATIVE_DEFAULT);
    }
    return characteristics;
  }

  private AssessmentResults performAssessment(XMLDocument swidDocuemnt, SWIDTagCharacteristics swidTagCharacteristics)
      throws AssessmentException {
    // setup the document assessment
    AssessmentExecutor<XMLDocument> executor
        = manager.getAssessmentExecutor(swidTagCharacteristics.getTagType(), swidTagCharacteristics.isAuthoritative());
    AssessmentResultBuilder assessmentResultBuilder = SWIDAssessmentResultBuilderFactory
        .newAssessmentResultBuilder(swidTagCharacteristics.getTagType(), swidTagCharacteristics.isAuthoritative());

    // do the assessment
    executor.execute(swidDocuemnt, assessmentResultBuilder);

    // generate the results
    return assessmentResultBuilder.end().build(SWIDRequirementsManager.getInstance());
  }
}
