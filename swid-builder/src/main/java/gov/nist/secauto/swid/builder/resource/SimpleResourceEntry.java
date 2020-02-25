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

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SimpleResourceEntry implements ResourceEntry {
  private String path;
  private Long size;
  private String version;
  private Map<HashAlgorithm, List<Byte>> digestValues;

  /**
   * Construct a new {@link ResourceEntry} using simple provided values.
   * 
   * @param path
   *          a path with '/' separators
   * @param digestValues
   *          a mapping of digest algorithms to bytes based on the resources contents, which may be
   *          {@code null} or empty
   * @param size
   *          the size in bytes of the resource contents, which may be {@code null}
   * @param version
   *          the version of the resource, which may be {@code null}
   */
  public SimpleResourceEntry(String path, Map<HashAlgorithm, List<Byte>> digestValues, Long size, String version) {
    Util.requireNonEmpty(path, "path");

    if (version != null) {
      Util.requireNonEmpty(version, "version");
    }

    if (digestValues == null) {
      this.digestValues = Collections.emptyMap();
    } else {
      this.digestValues = Collections.unmodifiableMap(digestValues);
    }
    this.path = path;
    this.size = size;
    this.version = version;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public List<Byte> getDigestValue(HashAlgorithm key) {
    return digestValues.get(key);
  }

  @Override
  public Map<HashAlgorithm, List<Byte>> getDigestValues() {
    return Collections.unmodifiableMap(digestValues);
  }

  /**
   * Set the path for this entry.
   * 
   * @param path
   *          the path to set
   * @return the current instance
   * @throws NullPointerException
   *           if the provided argument was {@code null}
   * @throws IllegalArgumentException
   *           if the provided argument is empty
   */
  public SimpleResourceEntry path(String path) {
    Util.requireNonEmpty(path, "path");
    this.path = path;
    return this;
  }

  /**
   * Set the size in bytes for this entry.
   * 
   * @param size
   *          the size to set
   * @return the current instance
   */
  public SimpleResourceEntry size(long size) {
    this.size = size;
    return this;
  }

  /**
   * Set the version for this entry.
   * 
   * @param version
   *          the version to set
   * @return the current instance
   * @throws NullPointerException
   *           if the provided argument was {@code null}
   * @throws IllegalArgumentException
   *           if the provided argument is empty
   */
  public SimpleResourceEntry version(String version) {
    Util.requireNonEmpty(version, "version");
    this.version = version;
    return this;
  }

  /**
   * Associate a list of bytes representing a digest with the hash function used to generate the
   * digest for this entry.
   * 
   * @param algorithm
   *          the hash function to set
   * @param bytes
   *          the list of bytes representing the digest value
   * @return the current instance
   * @throws NullPointerException
   *           if the provided argument was {@code null}
   * @throws IllegalArgumentException
   *           if the provided argument is empty
   */
  public SimpleResourceEntry digestValue(HashAlgorithm algorithm, List<Byte> bytes) {
    this.digestValues.put(algorithm, bytes);
    return this;
  }

}
