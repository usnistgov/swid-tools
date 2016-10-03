/**
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 17 United States Code Section 105, works of NIST employees are
 * not subject to copyright protection in the United States and are considered to
 * be in the public domain. Permission to freely use, copy, modify, and distribute
 * this software and its documentation without fee is hereby granted, provided that
 * this notice and disclaimer of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE. IN NO EVENT
 * SHALL NASA BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR
 * IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.decima.swid;

import gov.nist.decima.core.AssessmentReactor;
import gov.nist.decima.core.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.core.assessment.result.DefaultLoggingHandler;

import java.util.Objects;

public class SWIDAssessmentReactor extends AssessmentReactor {
  protected static final String PROPERTY_KEY_AUTHORITATIVE = "authoritative";
  protected static final String PROPERTY_KEY_TAG_TYPE = "tag-type";

  private final TagType tagType;
  private final boolean authoritative;

  /**
   * An {@link AssessmentReactor} tailored to the SWID tag requirements model.
   * 
   * @param tagType
   *          the type of tag to validate (i.e., primary, patch, corpus, supplemental)
   * @param authoritative
   *          {@code true} if the tag is produced by a 1st or 2nd party software provider, or
   *          {@code false} otherwise
   */
  public SWIDAssessmentReactor(TagType tagType, boolean authoritative) {
    super(SWIDRequirementsManager.getInstance());
    Objects.requireNonNull(tagType, "tagType");
    this.tagType = tagType;
    this.authoritative = authoritative;
  }

  @Override
  protected AssessmentResultBuilder newAssessmentResultBuilder() {
    AssessmentResultBuilder retval = new AssessmentResultBuilder(new SWIDValResultStatusBehavior(tagType, true));
    retval.setLoggingHandler(new DefaultLoggingHandler(getRequirementsManager()));
    retval.assignProperty(PROPERTY_KEY_AUTHORITATIVE, Boolean.toString(authoritative));
    retval.assignProperty(PROPERTY_KEY_TAG_TYPE, tagType.getName());
    return retval;
  }

}
