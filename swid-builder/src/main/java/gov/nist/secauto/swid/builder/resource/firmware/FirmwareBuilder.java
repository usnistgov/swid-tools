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

import gov.nist.secauto.swid.builder.ValidationException;
import gov.nist.secauto.swid.builder.resource.AbstractResourceBuilder;
import gov.nist.secauto.swid.builder.resource.ResourceCollectionEntryGenerator;
import gov.nist.secauto.swid.builder.util.Util;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class FirmwareBuilder
    extends AbstractResourceBuilder<FirmwareBuilder> {
  private FirmwareIdentifier id;
  private ZonedDateTime creationTimestamp = ZonedDateTime.now();
  private BigInteger version;
  private String description;
  private byte[] nonce;
  private List<ResourceReference> aliases = new LinkedList<>();
  private List<ResourceReference> dependencies = new LinkedList<>();
  private String blockDeviceIdentifier;
  private DeviceIdentifier targetDeviceIdentifier;
  private List<FirmwarePayloadBuilder> payloads = new LinkedList<>();
  private Map<Integer, byte[]> extensions = new LinkedHashMap<>();

  public FirmwareBuilder() {
    super();
    byte[] nonceData = new byte[8];
    new Random().nextBytes(nonceData);
    this.nonce = nonceData;
  }

  @Override
  public <T> void accept(T parentContext, ResourceCollectionEntryGenerator<T> creator) {
    creator.generate(parentContext, this);
  }

  /**
   * Retrieve the identifier of the firmware manifest.
   * 
   * @return the id
   */
  public FirmwareIdentifier getId() {
    return id;
  }

  /**
   * Set the id of the firmware manifest.
   * 
   * @param id
   *          the firmware's identifier
   * @return the same builder instance
   */
  public FirmwareBuilder id(FirmwareIdentifier id) {
    Objects.requireNonNull(id, "id");
    this.id = id;
    return this;
  }

  /**
   * Retrieve the identifier of the firmware manifest.
   * 
   * @return the creationTimestamp
   */
  public ZonedDateTime getCreationTimestamp() {
    return creationTimestamp;
  }

  /**
   * Set the creation timestamp of the firmware manifest.
   * 
   * @param dateTime
   *          the creationTimestamp to set
   * @return the same builder instance
   */
  public FirmwareBuilder creationTimestamp(ZonedDateTime dateTime) {
    Objects.requireNonNull(dateTime, "dateTime");
    this.creationTimestamp = dateTime;
    return this;
  }

  /**
   * Retrieve the version of the firmware manifest.
   * 
   * @return the version
   */
  public BigInteger getVersion() {
    return version;
  }

  /**
   * Set the version of the firmware manifest.
   * 
   * @param version
   *          the firmware's version
   * @return the same builder instance
   */
  public FirmwareBuilder version(BigInteger version) {
    Objects.requireNonNull(version, "version");
    this.version = version;
    return this;
  }

  /**
   * Retrieve the description of the firmware manifest.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the version of the firmware manifest.
   * 
   * @param description
   *          the firmware's description
   * @return the same builder instance
   */
  public FirmwareBuilder description(String description) {
    Util.requireNonEmpty(description, "description");
    this.description = description;
    return this;
  }

  /**
   * Retrieve the nonce of the firmware manifest.
   * 
   * @return the nonce
   */
  public byte[] getNonce() {
    return nonce;
  }

  /**
   * Set the nonce of the firmware manifest.
   * 
   * @param nonce
   *          the firmware's nonce
   * @return the same builder instance
   */
  public FirmwareBuilder nonce(byte[] nonce) {
    Objects.requireNonNull(nonce, "nonce");
    this.nonce = nonce;
    return this;
  }

  /**
   * Retrieve the aliases of the firmware manifest.
   * 
   * @return the aliases
   */
  public List<ResourceReference> getAliases() {
    return aliases;
  }

  /**
   * Adds a new alias to the list of aliases.
   * 
   * @param alias
   *          the alias to add
   * @return the same builder instance
   */
  public FirmwareBuilder addAlias(ResourceReference alias) {
    Objects.requireNonNull(alias, "alias");
    this.aliases.add(alias);
    return this;
  }

  /**
   * Retrieve the dependencies of the firmware manifest.
   * 
   * @return the dependencies
   */
  public List<ResourceReference> getDependencies() {
    return dependencies;
  }

  /**
   * Adds a new dependency to the list of aliases.
   * 
   * @param dependency
   *          the dependency to add
   * @return the same builder instance
   */
  public FirmwareBuilder addDependency(ResourceReference dependency) {
    Objects.requireNonNull(dependency, "dependency");
    this.dependencies.add(dependency);
    return this;
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
   * Retrieve the target device identifier for the firmware manifest.
   * 
   * @return the targetDeviceIdentifier
   */
  public DeviceIdentifier getTargetDeviceIdentifier() {
    return targetDeviceIdentifier;
  }

  /**
   * Set the target device identifier.
   * 
   * @param targetDeviceIdentifier
   *          a {@code non-null} target device identifier
   * @return the same builder instance
   */
  public FirmwareBuilder targetDeviceIdentifier(DeviceIdentifier targetDeviceIdentifier) {
    Objects.requireNonNull(targetDeviceIdentifier, "targetDeviceIdentifier");
    this.targetDeviceIdentifier = targetDeviceIdentifier;
    return this;
  }

  /**
   * Retrieve the firmware payloads of the firmware manifest.
   * 
   * @return the payloads
   */
  public List<FirmwarePayloadBuilder> getPayloads() {
    return payloads;
  }

  /**
   * Adds a new payload to the list of payloads.
   * 
   * @param payload
   *          the payload to add
   * @return the same builder instance
   */
  public FirmwareBuilder addPayload(FirmwarePayloadBuilder payload) {
    Objects.requireNonNull(payload, "payload");
    this.payloads.add(payload);
    return this;
  }

  /**
   * Retrieve the firmware manifest extensions.
   * 
   * @return the extensions
   */
  public Map<Integer, byte[]> getExtensions() {
    return extensions;
  }

  /**
   * Adds a new extension object.
   * 
   * @param extenstionType
   *          the extension type identifier
   * @param extension
   *          the extension content
   * @return the same builder instance
   */
  public FirmwareBuilder addExtension(int extenstionType, byte[] extension) {
    this.extensions.put(extenstionType, extension);
    return this;
  }

  @Override
  public void validate() throws ValidationException {
    validateNonNull("id", id);
    validateNonNull("creationTimestamp", creationTimestamp);
    validateNonNull("version", version);
    validateNonEmpty("nonce", nonce);
    validateNonNull("targetDeviceIdentifier", nonce);
  }

  public static FirmwareBuilder create() {
    return new FirmwareBuilder();
  }

}
