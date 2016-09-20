package gov.nist.swidval.controller;

import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.document.XMLDocument;
import gov.nist.decima.swid.SWIDAssessmentFactory;
import gov.nist.decima.swid.TagType;

public class SWIDAssessmentManager {
	private final ExecutorService executorService;
	private final EnumMap<TagType, AssessmentExecutor> assessmentExecutors;
	
	public SWIDAssessmentManager() {
		this(Executors.newFixedThreadPool(2));
	}

	public SWIDAssessmentManager(ExecutorService executorService) {
		this.executorService = executorService;
		this.assessmentExecutors = initializeAssessments();
	}

	private EnumMap<TagType, AssessmentExecutor> initializeAssessments() {
		EnumMap<TagType, AssessmentExecutor> retval = new EnumMap<>(TagType.class);
		for (TagType tagType : TagType.values()) {
			AssessmentExecutor executor = SWIDAssessmentFactory.getInstance().newAssessmentExecutor(tagType, true, executorService);
			retval.put(tagType, executor);
		}
		return retval;
	}

	public AssessmentExecutor getAssessmentExecutor(TagType tagType) {
		Objects.requireNonNull(tagType, "tagType");
		return assessmentExecutors.get(tagType);
	}

	public AssessmentResults performAssessment(XMLDocument document, TagType tagType) throws AssessmentException {
		AssessmentExecutor executor = getAssessmentExecutor(tagType);
		return executor.execute(document);
	}
}
