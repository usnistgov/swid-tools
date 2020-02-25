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
package gov.nist.swid.builder.resource.firmware;

import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.HashUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class FirmwarePayloadDigest {
  private final DigestType type;
  private final HashAlgorithm algorithm;
  private final byte[] value;
  private final byte[] guidance;

  public FirmwarePayloadDigest(DigestType type, HashAlgorithm algorithm, File file)
      throws NoSuchAlgorithmException, IOException {
    this(type, algorithm, HashUtils.hash(algorithm, file));
  }

  public FirmwarePayloadDigest(DigestType type, HashAlgorithm algorithm, File file, byte[] guidance)
      throws NoSuchAlgorithmException, IOException {
    this(type, algorithm, HashUtils.hash(algorithm, file), guidance);
  }

  public FirmwarePayloadDigest(DigestType type, HashAlgorithm algorithm, InputStream is)
      throws NoSuchAlgorithmException, IOException {
    this(type, algorithm, HashUtils.hash(algorithm, is));
  }

  public FirmwarePayloadDigest(DigestType type, HashAlgorithm algorithm, InputStream is, byte[] guidance)
      throws NoSuchAlgorithmException, IOException {
    this(type, algorithm, HashUtils.hash(algorithm, is), guidance);
  }

  public FirmwarePayloadDigest(DigestType type, HashAlgorithm algorithm, byte[] value) {
    this(type, algorithm, value, null);
  }

  public FirmwarePayloadDigest(DigestType type, HashAlgorithm algorithm, byte[] value, byte[] guidance) {
    super();
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(algorithm, "algorithm");
    Objects.requireNonNull(value, "value");
    this.type = type;
    this.algorithm = algorithm;
    this.value = value;
    this.guidance = guidance;
  }

  /**
   * @return the type
   */
  public DigestType getType() {
    return type;
  }

  /**
   * @return the algorithm
   */
  public HashAlgorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * @return the value
   */
  public byte[] getValue() {
    return value;
  }

  /**
   * @return the guidance
   */
  public byte[] getGuidance() {
    return guidance;
  }
}
