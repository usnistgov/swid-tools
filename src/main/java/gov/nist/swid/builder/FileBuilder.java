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

package gov.nist.swid.builder;

import static gov.nist.swid.builder.util.Util.requireNonEmpty;

import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.HashUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileBuilder extends AbstractFileSystemItemBuilder<FileBuilder> {
  private Long size;
  private String version;
  private Map<HashAlgorithm, String> hashAlgorithmToValueMap = new LinkedHashMap<>();

  protected FileBuilder() {
    super();
  }

  @Override
  public void reset() {
    super.reset();
    this.size = null;
    this.version = null;
    this.hashAlgorithmToValueMap = new LinkedHashMap<>();
  }

  public static FileBuilder create() {
    return new FileBuilder();
  }

  @Override
  public void accept(ResourceCollectionEntryGenerator creator) {
    creator.generate(this);
  }

  public Long getSize() {
    return size;
  }

  public String getVersion() {
    return version;
  }

  public Map<HashAlgorithm, String> getHashAlgorithmToValueMap() {
    return hashAlgorithmToValueMap;
  }

  /**
   * Sets the to-be-built file's size to the provided value.
   * 
   * @param size
   *          a non-zero integer indicating the file's size in bytes
   * @return the same builder instance
   */
  public FileBuilder size(long size) {
    if (size < 0) {
      throw new IllegalArgumentException("the size value must be a positive number");
    }
    this.size = size;
    return this;
  }

  /**
   * Sets the to-be-built file's hash value, for the provided algorithm, to the provided value. An
   * {@link InputStream} is used to retrieve the files contents to calculate the hash value. The
   * caller is resposnible for closing the stream used by this method.
   * 
   * @param algorithm
   *          the algorithm to establish a hash value for
   * @param is
   *          an {@link InputStream} that can be used to read the file
   * @return the same builder instance
   * @throws NoSuchAlgorithmException
   *           if the hash algorithm is not supported
   * @throws IOException
   *           if an error occurs while reading the stream
   */
  public FileBuilder hash(HashAlgorithm algorithm, InputStream is) throws NoSuchAlgorithmException, IOException {
    byte[] digest = HashUtils.hash(algorithm, is);
    String hashValue = HashUtils.toHexString(digest);
    return hash(algorithm, hashValue);
  }

  public FileBuilder hash(HashAlgorithm algorithm, String hashValue) {
    hashAlgorithmToValueMap.put(algorithm, hashValue);
    return this;
  }

  /**
   * Sets the to-be-built file's version to the provided value.
   * 
   * @param version the version value to use
   * @return the same builder instance
   */
  public FileBuilder version(String version) {
    requireNonEmpty(version, "version");
    this.version = version;
    return this;
  }

}
