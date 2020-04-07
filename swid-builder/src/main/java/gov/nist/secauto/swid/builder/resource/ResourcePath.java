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

package gov.nist.secauto.swid.builder.resource;

import gov.nist.secauto.swid.builder.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class ResourcePath implements Comparable<ResourcePath> {
  private final String path;
  private final List<String> segments;

  public ResourcePath(String path) {
    Util.requireNonEmpty(path, path);

    String[] segments = PathRelativizer.URI_SEPERATOR_PATTERN.split(path);
    List<String> segmentsList = new ArrayList<>(segments.length);
    for (String segment : segments) {
      segmentsList.add(segment);
    }
    this.path = path;
    this.segments = Collections.unmodifiableList(segmentsList);
  }

  public ResourcePath(String[] path) {
    this(Arrays.asList(path));
  }

  public ResourcePath(List<String> path) {
    this.path = String.join(PathRelativizer.URI_SEPERATOR, path);
    this.segments = Collections.unmodifiableList(path);
  }

  @Override
  public int hashCode() {
    return segments.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return segments.equals(obj);
  }

  @Override
  public int compareTo(ResourcePath that) {
    List<String> thisSegments = getSegments();
    List<String> thatSegments = that.getSegments();
    int retval = 0;
    for (int i = 0; i < thisSegments.size() && i < thatSegments.size(); i++) {
      String thisSegment = thisSegments.get(i);
      String thatSegment = thatSegments.get(i);
      if ((retval = thisSegment.compareTo(thatSegment)) != 0) {
        break;
      }
    }

    if (retval == 0) {
      retval = thisSegments.size() - thatSegments.size();
    }
    return retval;
  }

  public String getPath() {
    return path;
  }

  public List<String> getSegments() {
    return segments;
  }
}