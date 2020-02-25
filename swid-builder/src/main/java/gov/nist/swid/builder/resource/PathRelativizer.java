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
package gov.nist.swid.builder.resource;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class PathRelativizer {
  public static final Pattern URI_SEPERATOR_PATTERN = Pattern.compile("\\/");
  public static final String URI_SEPERATOR = "/";

  /**
   * Creates a relative sequence of path segments by 1) removing common path segments between the base
   * and target, 2) using ".." to change the context from the base to the target directory, and 3) use
   * of the remaining portion of the target path to complete the path segments.
   * 
   * @param base
   *          the base path to relativize from
   * @param target
   *          the target path to relativize to
   * @return a sequence of relative path segments pointing to the target from the base
   */
  public static List<String> relativize(URI base, URI target) {
    // Remove path segments within the path having . and .. segments
    base = base.normalize();
    target = target.normalize();

    return relativize(base.getPath(), target.getPath());
  }

  /**
   * Creates a relative sequence of path segments by 1) removing common path segments between the base
   * and target, 2) using ".." to change the context from the base to the target directory, and 3) use
   * of the remaining portion of the target path to complete the path segments.
   * 
   * @param base
   *          the base path to relativize from
   * @param target
   *          the target path to relativize to
   * @return a sequence of relative path segments pointing to the target from the base
   */
  public static List<String> relativize(String base, String target) {
    // Based on code from
    // http://stackoverflow.com/questions/10801283/get-relative-path-of-two-uris-in-java

    // Split paths into segments
    String[] baseSegments = URI_SEPERATOR_PATTERN.split(base);
    String[] targetSegments = URI_SEPERATOR_PATTERN.split(target);

    // Discard trailing segment of base path
    if (baseSegments.length > 0 && !base.endsWith("/")) {
      baseSegments = Arrays.copyOf(baseSegments, baseSegments.length - 1);
    }

    // Remove common prefix segments
    int segmentIndex = 0;
    while (segmentIndex < baseSegments.length && segmentIndex < targetSegments.length
        && baseSegments[segmentIndex].equals(targetSegments[segmentIndex])) {
      segmentIndex++;
    }

    // Construct the relative path
    int size = (baseSegments.length - segmentIndex) + (targetSegments.length - segmentIndex);
    List<String> retval = new ArrayList<>(size);
    for (int j = 0; j < (baseSegments.length - segmentIndex); j++) {
      retval.add("..");
    }

    for (int j = segmentIndex; j < targetSegments.length; j++) {
      retval.add(targetSegments[j]);
    }
    return Collections.unmodifiableList(retval);
  }

  /**
   * Converts a sequence of path segments to a relative URI.
   * 
   * @param relativePath
   *          the path segments to base the URI on
   * @return a relative URI
   */
  public static URI toURI(List<String> relativePath) {
    StringBuilder retval = new StringBuilder();
    for (String segment : relativePath) {
      if (retval.length() > 0) {
        retval.append("/");
      }
      retval.append(segment);
    }
    return URI.create(retval.toString());
  }

  /**
   * Normalizes path separators to '/'.
   * 
   * @param name
   *          a file name
   * @return a file path that has been normalized
   */
  public static String normalize(String name) {
    // This will normalize the path separators based on OS semantics
    File file = new File(name);
    String path = file.getPath();
    if (!File.pathSeparator.equals("/")) {
      // Paths are expected in a relative URI form
      path = path.replaceAll("/", "\\/");
      path = path.replaceAll("\\\\", "/");
    }
    return path;
  }

  /*
   * public static void main(String[] args) { String base = "SWIDTAG/swidtag5977492487829604934.swid";
   * String target = "lib/xml-resolver-1.2.jar"; URI baseUri = URI.create(base); URI targetUri =
   * URI.create(target); System.out.println("Base: " + baseUri); System.out.println("Target: " +
   * targetUri); System.out.println(baseUri.relativize(targetUri)); List<String> pathSegments =
   * relativize(base, target); System.out.println("Relative(String) segments: " + pathSegments);
   * System.out.println("Relative(String) URI: " + toURI(pathSegments)); pathSegments =
   * relativize(baseUri, targetUri); System.out.println("Relative(URI) segments: " + pathSegments);
   * System.out.println("Relative(URI) URI: " + toURI(pathSegments)); System.out.println("Normal: " +
   * PathRelativizer.normalize("bootstrap\\js\\jquery.filtertable.min.js"));
   * System.out.println("Normal: " + PathRelativizer.normalize("swidval-0.0.1-SNAPSHOT.jar"));
   * System.out.println("Normal: " + PathRelativizer.normalize("lib/core-0.0.1-SNAPSHOT.jar")); }
   */
}
