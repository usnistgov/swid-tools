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

package gov.nist.secauto.swid.builder;

import java.util.HashMap;
import java.util.Map;

public interface VersionScheme {
  Integer getIndex();

  String getName();

  static final Map<String, VersionScheme> byValueMap = new HashMap<>();

  static final Map<Integer, VersionScheme> byIndexMap = new HashMap<>();

  /**
   * Initialize the mapping of version scheme index and text values.
   * 
   * @param index
   *          the index position
   * @param value
   *          the human-readable value
   */
  default void init(Integer index, String value) {
    if (index != null) {
      synchronized (byIndexMap) {
        byIndexMap.put(index, this);
      }
    }
    synchronized (byValueMap) {
      byValueMap.put(value, this);
    }
  }

  public static VersionScheme assignPrivateVersionScheme(int indexValue, String name) {
    // force initialization of the known roles
    KnownVersionScheme.values();
    VersionScheme retval = null;
    synchronized (byValueMap) {
      @SuppressWarnings("unlikely-arg-type")
      VersionScheme value = byValueMap.get(indexValue);
      retval = value;
    }
    if (retval == null) {
      retval = new UnknownVersionScheme(indexValue, name);
    } else if (retval.getName().equals(name)) {
      // return the current role
    } else {
      throw new IllegalStateException("the version scheme with the name '" + retval.getName()
          + "' is already assigned to the index value '" + indexValue + "'");
    }
    return retval;
  }

  /**
   * Lookup a version scheme by an index value.F
   * 
   * @param value
   *          the index value to search for
   * @return the matching role or {@code null} if no matching role was not found.
   */
  public static VersionScheme lookupByIndex(int value) {
    // force initialization of the known roles
    KnownVersionScheme.values();
    VersionScheme retval = null;
    synchronized (byIndexMap) {
      retval = byIndexMap.get(value);
    }
    return retval;
  }

  /**
   * Lookup a version scheme by a human-readable name.
   * 
   * @param name
   *          the name to lookup
   * @return the matching version scheme or {@code null} if no matching version scheme was not found.
   */
  public static VersionScheme lookupByName(String name) {
    // force initialization of the known roles
    KnownVersionScheme.values();
    VersionScheme retval = null;
    synchronized (byValueMap) {
      retval = byValueMap.get(name);
    }

    if (retval == null) {
      retval = new UnknownVersionScheme(name);
    }
    return retval;
  }

}
