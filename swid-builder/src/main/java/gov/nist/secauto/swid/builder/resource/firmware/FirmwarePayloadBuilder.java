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

package gov.nist.secauto.swid.builder.resource.firmware;

import gov.nist.secauto.swid.builder.AbstractBuilder;
import gov.nist.secauto.swid.builder.ValidationException;
import gov.nist.secauto.swid.builder.resource.HashAlgorithm;
import gov.nist.secauto.swid.builder.resource.HashUtils;
import gov.nist.secauto.swid.builder.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FirmwarePayloadBuilder
    extends AbstractBuilder {

  private FirmwareIdentifier id;
  private String packageIdentifier;
  private String description;
  private Integer formatType;
  private byte[] formatGuidance;
  private BigInteger size;
  private BigInteger simpleVersion;
  // private FirmwarePayloadVersion version;
  private List<FirmwarePayloadDigest> digests = new LinkedList<>();
  private String componentIndex;
  private FirmwareIdentifier storageId;
  private List<FirmwareRequirement<FirmwareCondition>> conditions = new LinkedList<>();
  private List<FirmwareRequirement<FirmwareDirective>> directives = new LinkedList<>();
  // private VersionedDependency targetDependency;
  // private MinimalVersion targetMinimalVersion;
  // private List<FirmwarePayloadRelationship> relationships;
  private FirmwarePayloadPackage firmwarePackage;

  /**
   * Retrieve the identifier of the firmware payload.
   * 
   * @return the id
   */
  public FirmwareIdentifier getId() {
    return id;
  }

  /**
   * Set the id of the firmware payload.
   * 
   * @param id
   *          the firmware's identifier
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder id(FirmwareIdentifier id) {
    Objects.requireNonNull(id, "id");
    this.id = id;
    return this;
  }

  /**
   * Retrieve the package identifier of the firmware manifest.
   * 
   * @return the packageIdentifier
   */
  public String getPackageIdentifier() {
    return packageIdentifier;
  }

  /**
   * Set the package identifier.
   * 
   * @param packageIdentifier
   *          a {@code non-null} package identifier
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder packageIdentifier(String packageIdentifier) {
    Util.requireNonEmpty(packageIdentifier, "packageIdentifier");
    this.packageIdentifier = packageIdentifier;
    return this;
  }

  /**
   * Retrieve the description of the firmware payload.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the version of the firmware payload.
   * 
   * @param description
   *          the firmware's description
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder description(String description) {
    Util.requireNonEmpty(description, "description");
    this.description = description;
    return this;
  }

  /**
   * Retrieve the format type of the firmware payload.
   * 
   * @return the type
   */
  public int getFormatType() {
    return formatType;
  }

  /**
   * Set the format type.
   * 
   * @param type
   *          a format type
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder formatType(int type) {
    this.formatType = type;
    return this;
  }

  /**
   * Retrieve the firmware payload's format guidance.
   * 
   * @return the format guidance
   */
  public byte[] getFormatGuidance() {
    return formatGuidance;
  }

  /**
   * Set the firmware payload's format guidance.
   * 
   * @param bytes
   *          guidance a {@code non-null} byte string
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder formatGuidance(byte[] bytes) {
    Objects.requireNonNull(bytes, "bytes");
    this.formatGuidance = bytes;
    return this;
  }

  /**
   * Retrieve the size the firmware payload.
   * 
   * @return the size
   */
  public BigInteger getSize() {
    return size;
  }

  /**
   * Set the size.
   * 
   * @param size
   *          a {@code non-null} size
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder size(BigInteger size) {
    Objects.requireNonNull(size, "size");
    this.size = size;
    return this;
  }

  /**
   * Retrieve the simple version of the firmware payload.
   * 
   * @return the version
   */
  public BigInteger getSimpleVersion() {
    return simpleVersion;
  }

  // /**
  // * Set the version of the payload.
  // *
  // * @param version
  // * a {@code non-null} version
  // * @return the same builder instance
  // */
  // public FirmwarePayloadBuilder version(FirmwarePayloadVersion version) {
  // Objects.requireNonNull(version, "version");
  // this.version = version;
  // return this;
  // }
  //
  // /**
  // * Retrieve the simple version of the firmware payload.
  // *
  // * @return the version
  // */
  // public FirmwarePayloadVersion getVersion() {
  // return version;
  // }

  /**
   * Set the simple version of the payload.
   * 
   * @param version
   *          a {@code non-null} version
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder simpleVersion(BigInteger version) {
    Objects.requireNonNull(version, "version");
    this.simpleVersion = version;
    return this;
  }

  /**
   * Retrieve the digests of the firmware payload.
   * 
   * @return the digests
   */
  public List<FirmwarePayloadDigest> getDigests() {
    return digests;
  }

  /**
   * Adds a new digest to the list of digests.
   * 
   * @param digest
   *          the digest to add
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder addDigest(FirmwarePayloadDigest digest) {
    Objects.requireNonNull(digest, "digest");
    this.digests.add(digest);
    return this;
  }

  /**
   * Sets the to-be-built file's hash value, for the provided algorithm, to the provided value. An
   * {@link InputStream} is used to retrieve the files contents to calculate the hash value. The
   * caller is responsible for closing the stream used by this method.
   * 
   * @param type
   *          the type of resource this digest is for
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
  public FirmwarePayloadBuilder addDigest(DigestType type, HashAlgorithm algorithm, File file)
      throws NoSuchAlgorithmException, IOException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
      return addDigest(type, algorithm, is);
    }
  }

  /**
   * Sets the file's hash value, for the provided algorithm, to the provided value. An
   * {@link InputStream} is used to retrieve the files contents to calculate the hash value. The
   * caller is responsible for closing the stream used by this method.
   * 
   * @param type
   *          the type of resource this digest is for
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
  public FirmwarePayloadBuilder addDigest(DigestType type, HashAlgorithm algorithm, InputStream is)
      throws NoSuchAlgorithmException, IOException {
    byte[] digest = HashUtils.hash(algorithm, is);
    return addDigest(type, algorithm, digest);
  }

  /**
   * Sets the file's hash value, for the provided algorithm, to the provided value.
   * 
   * @param type
   *          the type of resource this digest is for
   * @param algorithm
   *          the algorithm to establish a hash value for
   * @param digest
   *          an array of bytes representing the digest value
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder addDigest(DigestType type, HashAlgorithm algorithm, byte[] digest) {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(algorithm, "algorithm");
    Objects.requireNonNull(digest, "digest");

    FirmwarePayloadDigest payloadDigest = new FirmwarePayloadDigest(type, algorithm, digest);
    return addDigest(payloadDigest);
  }

  /**
   * Retrieve the index value of the firmware component.
   * 
   * @return the componentIndex
   */
  public String getComponentIndex() {
    return componentIndex;
  }

  /**
   * Set the component index.
   * 
   * @param componentIndex
   *          a {@code non-null} component index
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder componentIndex(String componentIndex) {
    Util.requireNonEmpty(componentIndex, "componentIndex");
    this.componentIndex = componentIndex;
    return this;
  }

  /**
   * Retrieve the storage id of the firmware component.
   * 
   * @return the storage id
   */
  public FirmwareIdentifier getStorageId() {
    return storageId;
  }

  /**
   * Set the storage id.
   * 
   * @param id
   *          a {@code non-null} storage id
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder storageId(FirmwareIdentifier id) {
    Objects.requireNonNull(id, "storageId");
    this.storageId = id;
    return this;
  }

  /**
   * Retrieve the conditions of the firmware payload.
   * 
   * @return the conditions
   */
  public List<FirmwareRequirement<FirmwareCondition>> getConditions() {
    return conditions;
  }

  /**
   * Adds a new condition to the list of conditions.
   * 
   * @param condition
   *          the condition to add
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder addCondition(FirmwareRequirement<FirmwareCondition> condition) {
    Objects.requireNonNull(condition, "condition");
    this.conditions.add(condition);
    return this;
  }

  /**
   * Retrieve the directives of the firmware payload.
   * 
   * @return the directives
   */
  public List<FirmwareRequirement<FirmwareDirective>> getDirectives() {
    return directives;
  }

  /**
   * Adds a new directive to the list of directives.
   * 
   * @param directive
   *          the directive to add
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder addDirective(FirmwareRequirement<FirmwareDirective> directive) {
    Objects.requireNonNull(directive, "directive");
    this.directives.add(directive);
    return this;
  }

  // /**
  // * Retrieve the targetDependency of the firmware component.
  // *
  // * @return the targetDependency
  // */
  // public VersionedDependency getTargetDependency() {
  // return targetDependency;
  // }
  //
  // /**
  // * Set the component targetDependency.
  // *
  // * @param targetDependency
  // * a {@code non-null} targetDependency
  // * @return the same builder instance
  // */
  // public FirmwarePayloadBuilder targetDependency(VersionedDependency
  // targetDependency) {
  // Objects.requireNonNull(targetDependency, "targetDependency");
  // this.targetDependency = targetDependency;
  // return this;
  // }

  // /**
  // * Retrieve the targetMinimalVersion of the firmware component.
  // *
  // * @return the targetMinimalVersion
  // */
  // public MinimalVersion getTargetMinimalVersion() {
  // return targetMinimalVersion;
  // }
  //
  // /**
  // * Set the component targetMinimalVersion.
  // *
  // * @param targetMinimalVersion
  // * a {@code non-null} targetMinimalVersion
  // * @return the same builder instance
  // */
  // public FirmwarePayloadBuilder targetMinimalVersion(MinimalVersion
  // targetMinimalVersion) {
  // Objects.requireNonNull(targetDependency, "targetDependency");
  // this.targetMinimalVersion = targetMinimalVersion;
  // return this;
  // }

  // /**
  // * Retrieve the relationships of the firmware payload.
  // *
  // * @return the relationships
  // */
  // public List<FirmwarePayloadRelationship> getRelationships() {
  // return relationships;
  // }
  //
  // /**
  // * Adds a new relationship to the list of relationships.
  // *
  // * @param relationship
  // * the relationship to add
  // * @return the same builder instance
  // */
  // public FirmwarePayloadBuilder addRelationship(FirmwarePayloadRelationship
  // relationship) {
  // Objects.requireNonNull(relationship, "relationship");
  // this.relationships.add(relationship);
  // return this;
  // }

  /**
   * Retrieve the firmwarePackage of the firmware component.
   * 
   * @return the targetMinimalVersion
   */
  public FirmwarePayloadPackage getFirmwarePackage() {
    return firmwarePackage;
  }

  /**
   * Set the component firmwarePackage.
   * 
   * @param firmwarePackage
   *          a {@code non-null} firmwarePackage
   * @return the same builder instance
   */
  public FirmwarePayloadBuilder firmwarePackage(FirmwarePayloadPackage firmwarePackage) {
    Objects.requireNonNull(firmwarePackage, "firmwarePackage");
    this.firmwarePackage = firmwarePackage;
    return this;
  }

  @Override
  public void validate() throws ValidationException {
    validateNonNull("id", id);
    validateNonNull("formatType", formatType);
    validateNonNull("size", size);
    validateNonEmpty("digests", digests);
    validateNonNull("storageId", storageId);
    validateNonEmpty("conditions", conditions);
  }

  @Override
  public void reset() {
    // TODO: implement
  }
}
