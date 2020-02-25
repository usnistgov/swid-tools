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
  private final EnumMap<TagType, AssessmentExecutor<XMLDocument>> authoritativeAssessmentExecutors
      = new EnumMap<>(TagType.class);
  private final EnumMap<TagType, AssessmentExecutor<XMLDocument>> nonAuthoritativeAssessmentExecutors
      = new EnumMap<>(TagType.class);

  public SWIDAssessmentManager() {
    this(Executors.newFixedThreadPool(2));
  }

  public SWIDAssessmentManager(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public synchronized AssessmentExecutor<XMLDocument> getAssessmentExecutor(TagType tagType, boolean authoritative) {
    Objects.requireNonNull(tagType, "tagType");

    EnumMap<TagType, AssessmentExecutor<XMLDocument>> executorMap;
    if (authoritative) {
      executorMap = authoritativeAssessmentExecutors;
    } else {
      executorMap = nonAuthoritativeAssessmentExecutors;
    }

    AssessmentExecutor<XMLDocument> executor = executorMap.get(tagType);
    if (executor == null) {
      executor = SWIDAssessmentFactory.getInstance().newAssessmentExecutor(tagType, authoritative, executorService);
      executorMap.put(tagType, executor);
    }
    return executor;
  }
}
