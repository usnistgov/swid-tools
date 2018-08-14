
package gov.nist.swidval.controller;

import gov.nist.decima.core.assessment.AssessmentExecutor;
import gov.nist.decima.swid.SWIDAssessmentFactory;
import gov.nist.decima.swid.TagType;
import gov.nist.decima.xml.document.XMLDocument;

import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SWIDAssessmentManager {
  private final ExecutorService executorService;
  private final EnumMap<TagType, AssessmentExecutor<XMLDocument>> assessmentExecutors;

  public SWIDAssessmentManager() {
    this(Executors.newFixedThreadPool(2));
  }

  public SWIDAssessmentManager(ExecutorService executorService) {
    this.executorService = executorService;
    this.assessmentExecutors = initializeAssessments();
  }

  private EnumMap<TagType, AssessmentExecutor<XMLDocument>> initializeAssessments() {
    EnumMap<TagType, AssessmentExecutor<XMLDocument>> retval = new EnumMap<>(TagType.class);
    for (TagType tagType : TagType.values()) {
      AssessmentExecutor<XMLDocument> executor
          = SWIDAssessmentFactory.getInstance().newAssessmentExecutor(tagType, true, executorService);
      retval.put(tagType, executor);
    }
    return retval;
  }

  public AssessmentExecutor<XMLDocument> getAssessmentExecutor(TagType tagType) {
    Objects.requireNonNull(tagType, "tagType");
    return assessmentExecutors.get(tagType);
  }
}
