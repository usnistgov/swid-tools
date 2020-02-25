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
package gov.nist.decima.swid;

import gov.nist.decima.core.assessment.result.ResultStatusBehavior;
import gov.nist.decima.core.requirement.Requirement;

import java.util.Map;
import java.util.Set;

public class SWIDValResultStatusBehavior implements ResultStatusBehavior {
  private static final String TAG_TYPES_QNAME = "{" + XMLConstants.REQUIREMENTS_SWID_EXTENSION_NS + "}tag-type";
  private static final String SCOPE_QNAME = "{" + XMLConstants.REQUIREMENTS_SWID_EXTENSION_NS + "}scope";
  private final TagType tagType;
  private final boolean authoritative;

  public SWIDValResultStatusBehavior(TagType tagType, boolean authoritative) {
    this.tagType = tagType;
    this.authoritative = authoritative;
  }

  @Override
  public boolean isInScope(Requirement requirement) {
    Map<String, Set<String>> tags = requirement.getMetadataTagValueMap();

    Set<String> tagTypes = tags.get(TAG_TYPES_QNAME);
    Set<String> scopeValues = tags.get(SCOPE_QNAME);
    String scope = authoritative ? "authoritative" : "non-authoritative";
    boolean matchTagType
        = tagTypes == null || tagTypes.isEmpty() || tagTypes.contains("all") || tagTypes.contains(tagType.getName());
    boolean matchScope
        = scopeValues == null || scopeValues.isEmpty() || scopeValues.contains("all") || scopeValues.contains(scope);
    return matchTagType && matchScope;
  }

}
