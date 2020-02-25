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

import gov.nist.secauto.decima.core.assessment.result.AssessmentResultBuilder;
import gov.nist.secauto.decima.core.assessment.result.DefaultAssessmentResultBuilder;
import gov.nist.secauto.decima.core.assessment.util.AssessmentLoggingHandler;
import gov.nist.secauto.decima.core.assessment.util.AssessmentSummarizingLoggingHandler;
import gov.nist.secauto.decima.core.assessment.util.LoggingHandler;
import gov.nist.secauto.decima.core.assessment.util.OverallSummaryLoggingHandler;
import gov.nist.secauto.decima.core.assessment.util.TestResultLoggingHandler;

import org.apache.logging.log4j.Level;

import java.util.Objects;

public class SWIDAssessmentResultBuilderFactory {
  protected static final String PROPERTY_KEY_AUTHORITATIVE = "authoritative";
  protected static final String PROPERTY_KEY_TAG_TYPE = "tag-type";

  /**
   * Creates a new An {@link AssessmentResultBuilder} tailored to the SWID tag requirements model.
   * 
   * @param tagType
   *          the type of tag to validate (i.e., primary, patch, corpus, supplemental)
   * @param authoritative
   *          {@code true} if the tag is produced by a 1st or 2nd party software provider, or
   *          {@code false} otherwise
   * @return the new builder
   */
  public static AssessmentResultBuilder newAssessmentResultBuilder(TagType tagType, boolean authoritative) {
    Objects.requireNonNull(tagType, "tagType");

    LoggingHandler loggingHandler = new TestResultLoggingHandler(SWIDRequirementsManager.getInstance());
    loggingHandler = new AssessmentLoggingHandler(Level.INFO, loggingHandler);
    loggingHandler = new AssessmentSummarizingLoggingHandler(Level.INFO, loggingHandler);
    loggingHandler = new OverallSummaryLoggingHandler(Level.INFO, loggingHandler);
    DefaultAssessmentResultBuilder retval
        = new DefaultAssessmentResultBuilder(new SWIDValResultStatusBehavior(tagType, authoritative));
    retval.setLoggingHandler(loggingHandler);
    retval.assignProperty(PROPERTY_KEY_AUTHORITATIVE, Boolean.toString(authoritative));
    retval.assignProperty(PROPERTY_KEY_TAG_TYPE, tagType.getName());
    return retval;
  }
}
