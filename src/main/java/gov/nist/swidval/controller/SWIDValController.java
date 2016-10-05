package gov.nist.swidval.controller;

import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.LoggingAssessmentNotifier;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.assessment.result.ResultStatus;
import gov.nist.decima.core.assessment.schematron.SchematronAssessment;
import gov.nist.decima.core.document.JDOMDocument;
import gov.nist.decima.core.document.XMLDocumentException;
import gov.nist.decima.swid.SWIDAssessmentReactor;
import gov.nist.decima.swid.TagType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SWIDValController {
	private static final Logger log = LogManager.getLogger(SchematronAssessment.class);

	public static final String MODEL_KEY_ASSESSMENT_RESULT = "assessment";
	public static final String MODEL_KEY_FILENAME = "filename";

	private final SWIDAssessmentManager manager = new SWIDAssessmentManager();

//	@PostMapping("/validate.html")
	@RequestMapping("/validate")
	public ModelAndView validate(@RequestParam("file") MultipartFile file, @RequestParam("tag-type") String tagType) throws AssessmentException, UnrecognizedContentException, XMLDocumentException, IOException {
		if (file.isEmpty()) {
			throw new UnrecognizedContentException("A valid SWID tag was not provided.");
		}

		File tempFile = File.createTempFile("swid", ".swidtag");
		tempFile.deleteOnExit();
		file.transferTo(tempFile);

		TagType type = TagType.lookup(tagType);
		AssessmentExecutor executor = manager.getAssessmentExecutor(type);
		SWIDAssessmentReactor reactor = new SWIDAssessmentReactor(type, false);
		reactor.pushAssessmentExecution(new JDOMDocument(tempFile), executor);
		AssessmentResults results = reactor.react(new LoggingAssessmentNotifier());

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
		log.error("Requested URL '"+request.getRequestURL()+"' raised an exception:",ex);

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
//		ModelAndView modelAndView = new ModelAndView();
//	    modelAndView.addObject("exception", ex);
//	    modelAndView.addObject("url", request.getRequestURL());
//	    
//	    modelAndView.setViewName("error");
//	    return modelAndView;
	}
}
