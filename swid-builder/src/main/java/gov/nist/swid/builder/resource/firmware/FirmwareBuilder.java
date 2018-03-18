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
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR
 * IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.swid.builder.resource.firmware;

import gov.nist.swid.builder.ValidationException;
import gov.nist.swid.builder.resource.AbstractResourceBuilder;
import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.HashUtils;
import gov.nist.swid.builder.resource.ResourceCollectionEntryGenerator;
import gov.nist.swid.builder.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class FirmwareBuilder extends AbstractResourceBuilder<FirmwareBuilder> {
  private String name;
  private String version;
  private String packageIdentifier;
  private BigInteger componentIndex;
  private String blockDeviceIdentifier;
  private String targetHardwareIdentifier;
  private String modelLabel;
  private HashAlgorithm hashAlgorithm;
  private byte[] hashValue;
  private byte[] cmsFirmwarePackage;

  @Override
  public <T> void accept(ResourceCollectionEntryGenerator<T> creator, T parentContext) {
    creator.generate(this, parentContext);
  }

  /**
   * Retrieve the name of the firmware.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieve the version of the firmware.
   * 
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Retrieve the package identifier of the firmware.
   * 
   * @return the packageIdentifier
   */
  public String getPackageIdentifier() {
    return packageIdentifier;
  }

  /**
   * Retrieve the index value of the firmware component.
   * 
   * @return the componentIndex
   */
  public BigInteger getComponentIndex() {
    return componentIndex;
  }

  /**
   * Retrieve the block device identifier for the firmware.
   * 
   * @return the blockDeviceIdentifier
   */
  public String getBlockDeviceIdentifier() {
    return blockDeviceIdentifier;
  }

  /**
   * Retrieve the target hardware identifier for the firmware.
   * 
   * @return the targetHardwareIdentifier
   */
  public String getTargetHardwareIdentifier() {
    return targetHardwareIdentifier;
  }

  /**
   * Retrieve the model label for the firmware.
   * 
   * @return the modelLabel
   */
  public String getModelLabel() {
    return modelLabel;
  }

  /**
   * Retrieve the hash algorithm used to verify the firmware.
   * 
   * @return the hashAlgorithm
   */
  public HashAlgorithm getHashAlgorithm() {
    return hashAlgorithm;
  }

  /**
   * Retrieve the hash value for the firmware.
   * 
   * @return the hashValue
   */
  public byte[] getHashValue() {
    return hashValue;
  }

  /**
   * Retrieve the actual firmware.
   * 
   * @return the cmsFirmwarePackage
   */
  public byte[] getCmsFirmwarePackage() {
    return cmsFirmwarePackage;
  }

  /**
   * Set the name of the firmware.
   * 
   * @param name
   *          the firmware's name
   * @return the same builder instance
   */
  public FirmwareBuilder name(String name) {
    Util.requireNonEmpty(name, "name");
    this.name = name;
    return this;
  }

  /**
   * Set the version of the firmware.
   * 
   * @param version
   *          the firmware's version
   * @return the same builder instance
   */
  public FirmwareBuilder version(String version) {
    Util.requireNonEmpty(version, "version");
    this.version = version;
    return this;
  }

  /**
   * Set the package identifier.
   * 
   * @param packageIdentifier
   *          a {@code non-null} package identifier
   * @return the same builder instance
   */
  public FirmwareBuilder packageIdentifier(String packageIdentifier) {
    Util.requireNonEmpty(packageIdentifier, "packageIdentifier");
    this.packageIdentifier = packageIdentifier;
    return this;
  }

  /**
   * Set the component index.
   * 
   * @param componentIndex
   *          a {@code non-null} component index
   * @return the same builder instance
   */
  public FirmwareBuilder componentIndex(BigInteger componentIndex) {
    Objects.requireNonNull(componentIndex, "componentIndex");
    this.componentIndex = componentIndex;
    return this;
  }

  /**
   * Set the block device identifier.
   * 
   * @param blockDeviceIdentifier
   *          a {@code non-null} block device identifier
   * @return the same builder instance
   */
  public FirmwareBuilder blockDeviceIdentifier(String blockDeviceIdentifier) {
    Util.requireNonEmpty(blockDeviceIdentifier, "blockDeviceIdentifier");
    this.blockDeviceIdentifier = blockDeviceIdentifier;
    return this;
  }

  /**
   * Set the target hardware identifier.
   * 
   * @param targetHardwareIdentifier
   *          a {@code non-null} target hardware identifier
   * @return the same builder instance
   */
  public FirmwareBuilder targetHardwareIdentifier(String targetHardwareIdentifier) {
    Util.requireNonEmpty(targetHardwareIdentifier, "targetHardwareIdentifier");
    this.targetHardwareIdentifier = targetHardwareIdentifier;
    return this;
  }

  /**
   * Set the model label.
   * 
   * @param modelLabel
   *          a {@code non-null} model label
   * @return the same builder instance
   */
  public FirmwareBuilder modelLabel(String modelLabel) {
    Util.requireNonEmpty(modelLabel, "modelLabel");
    this.modelLabel = modelLabel;
    return this;
  }

  /**
   * Set the firmware package.
   * 
   * @param cmsFirmwarePackage
   *          a {@code non-null} array of bytes containing the actual firmware
   * @return the same builder instance
   */
  public FirmwareBuilder cmsFirmwarePackage(byte[] cmsFirmwarePackage) {
    Objects.requireNonNull(cmsFirmwarePackage, "cmsFirmwarePackage");
    this.cmsFirmwarePackage = cmsFirmwarePackage;
    return this;
  }

  /**
   * Sets the to-be-built file's hash value, for the provided algorithm, to the provided value. An {@link InputStream}
   * is used to retrieve the files contents to calculate the hash value. The caller is resposnible for closing the
   * stream used by this method.
   * 
   * @param algorithm
   *          the algorithm to establish a hash value for
   * @param file
   *          the file to hash
   * @return the same builder instance
   * @throws NoSuchAlgorithmException
   *           if the hash algorithm is not supported
   * @throws IOException
   *           if an error occurs while reading the stream
   */
  public FirmwareBuilder hash(HashAlgorithm algorithm, File file) throws NoSuchAlgorithmException, IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(file));
    return hash(algorithm, is);
  }

  /**
   * Sets the file's hash value, for the provided algorithm, to the provided value. An {@link InputStream} is used to
   * retrieve the files contents to calculate the hash value. The caller is responsible for closing the stream used by
   * this method.
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
  public FirmwareBuilder hash(HashAlgorithm algorithm, InputStream is) throws NoSuchAlgorithmException, IOException {
    byte[] digest = HashUtils.hash(algorithm, is);
    return hash(algorithm, digest);
  }

  /**
   * Sets the file's hash value, for the provided algorithm, to the provided value.
   * 
   * @param algorithm
   *          the algorithm to establish a hash value for
   * @param hashBytes
   *          an rray of bytes representing the digest value
   * @return the same builder instance
   */
  public FirmwareBuilder hash(HashAlgorithm algorithm, byte[] hashBytes) {
    Objects.requireNonNull(algorithm, "algorithm");
    Objects.requireNonNull(hashBytes, "hashBytes");
    this.hashAlgorithm = algorithm;
    this.hashValue = hashBytes;
    return this;
  }

  @Override
  public void validate() throws ValidationException {
    validateNonEmpty("name", name);
    // validateNonEmpty("modelLabel", modelLabel);

    if (hashValue != null && hashValue.length == 0) {
      throw new ValidationException("the field 'hashValue' must contain a value with a non-zero length");
    }
  }

  public static FirmwareBuilder create() {
    return new FirmwareBuilder();
  }

}
