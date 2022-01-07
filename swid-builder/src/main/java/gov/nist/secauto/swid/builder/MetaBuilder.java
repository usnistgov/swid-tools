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

import static gov.nist.secauto.swid.builder.util.Util.requireNonEmpty;

public class MetaBuilder
    extends AbstractLanguageSpecificBuilder<MetaBuilder> {
  public static MetaBuilder create() {
    return new MetaBuilder();
  }

  private String activationStatus;
  private String channelType;
  private String colloquialVersion;
  private String description;
  private String edition;
  private String entitlementDataRequired;
  private String entitlementKey;
  private String generator;
  private String persistentId;
  private String productBaseName;
  private String productFamily;
  private String revision;
  private String summary;
  private String unspscCode;
  private String unspscVersion;

  protected MetaBuilder() {
    super();
  }

  @Override
  public void reset() {
    this.activationStatus = null;
    this.channelType = null;
    this.colloquialVersion = null;
    this.description = null;
    this.edition = null;
    this.entitlementDataRequired = null;
    this.entitlementKey = null;
    this.generator = null;
    this.persistentId = null;
    this.productBaseName = null;
    this.productFamily = null;
    this.revision = null;
    this.summary = null;
    this.unspscCode = null;
    this.unspscVersion = null;
  }

  /**
   * Retrieves the activation status.
   * 
   * @return the activationStatus or {@code null} if one hasn't been set
   */
  public String getActivationStatus() {
    return activationStatus;
  }

  /**
   * Sets the activation status.
   * 
   * @param activationStatus
   *          the activationStatus to set
   * @return the current builder instance
   */
  public MetaBuilder setActivationStatus(String activationStatus) {
    requireNonEmpty(activationStatus, "activationStatus");
    this.activationStatus = activationStatus;
    return this;
  }

  /**
   * Retrieves the channel type.
   * 
   * @return the channelType or {@code null} if one hasn't been set
   */
  public String getChannelType() {
    return channelType;
  }

  /**
   * Sets the channel type.
   * 
   * @param channelType
   *          the channelType to set
   * @return the current builder instance
   */
  public MetaBuilder setChannelType(String channelType) {
    requireNonEmpty(channelType, "channelType");
    this.channelType = channelType;
    return this;
  }

  /**
   * Retrieves the colloquial version.
   * 
   * @return the colloquialVersion or {@code null} if one hasn't been set
   */
  public String getColloquialVersion() {
    return colloquialVersion;
  }

  /**
   * Sets the colloquial version.
   * 
   * @param colloquialVersion
   *          the colloquialVersion to set
   * @return the current builder instance
   */
  public MetaBuilder setColloquialVersion(String colloquialVersion) {
    requireNonEmpty(colloquialVersion, "colloquialVersion");
    this.colloquialVersion = colloquialVersion;
    return this;
  }

  /**
   * Retrieves the description.
   * 
   * @return the description or {@code null} if one hasn't been set
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          the description to set
   * @return the current builder instance
   */
  public MetaBuilder setDescription(String description) {
    requireNonEmpty(description, "description");
    this.description = description;
    return this;
  }

  /**
   * Retrieves the edition.
   * 
   * @return the edition or {@code null} if one hasn't been set
   */
  public String getEdition() {
    return edition;
  }

  /**
   * Sets the edition.
   * 
   * @param edition
   *          the edition to set
   * @return the current builder instance
   */
  public MetaBuilder setEdition(String edition) {
    requireNonEmpty(edition, "edition");
    this.edition = edition;
    return this;
  }

  /**
   * Retrieves the indicator of if entitlement data is required.
   * 
   * @return the entitlementDataRequired or {@code null} if one hasn't been set
   */
  public String getEntitlementDataRequired() {
    return entitlementDataRequired;
  }

  /**
   * Sets the indicator of if entitlement data is required.
   * 
   * @param entitlementDataRequired
   *          the entitlementDataRequired to set
   * @return the current builder instance
   */
  public MetaBuilder setEntitlementDataRequired(String entitlementDataRequired) {
    requireNonEmpty(entitlementDataRequired, "entitlementDataRequired");
    this.entitlementDataRequired = entitlementDataRequired;
    return this;
  }

  /**
   * Retrieves the entitlement key.
   * 
   * @return the entitlementKey or {@code null} if one hasn't been set
   */
  public String getEntitlementKey() {
    return entitlementKey;
  }

  /**
   * Sets the entitlement key.
   * 
   * @param entitlementKey
   *          the entitlementKey to set
   * @return the current builder instance
   */
  public MetaBuilder setEntitlementKey(String entitlementKey) {
    requireNonEmpty(entitlementKey, "entitlementKey");
    this.entitlementKey = entitlementKey;
    return this;
  }

  /**
   * Retrieves the generator.
   * 
   * @return the generator or {@code null} if one hasn't been set
   */
  public String getGenerator() {
    return generator;
  }

  /**
   * Sets the generator.
   * 
   * @param generator
   *          the generator to set
   * @return the current builder instance
   */
  public MetaBuilder setGenerator(String generator) {
    requireNonEmpty(generator, "generator");
    this.generator = generator;
    return this;
  }

  /**
   * Retrieves the persistentId.
   * 
   * @return the persistentId or {@code null} if one hasn't been set
   */
  public String getPersistentId() {
    return persistentId;
  }

  /**
   * Sets the persistentId.
   * 
   * @param persistentId
   *          the persistentId to set
   * @return the current builder instance
   */
  public MetaBuilder setPersistentId(String persistentId) {
    requireNonEmpty(persistentId, "persistentId");
    this.persistentId = persistentId;
    return this;
  }

  /**
   * Retrieves the product base name.
   * 
   * @return the productBaseName or {@code null} if one hasn't been set
   */
  public String getProductBaseName() {
    return productBaseName;
  }

  /**
   * Sets the product base name.
   * 
   * @param productBaseName
   *          the productBaseName to set
   * @return the current builder instance
   */
  public MetaBuilder setProductBaseName(String productBaseName) {
    requireNonEmpty(productBaseName, "productBaseName");
    this.productBaseName = productBaseName;
    return this;
  }

  /**
   * Retrieves the product family.
   * 
   * @return the productFamily or {@code null} if one hasn't been set
   */
  public String getProductFamily() {
    return productFamily;
  }

  /**
   * Sets the product family.
   * 
   * @param productFamily
   *          the productFamily to set
   * @return the current builder instance
   */
  public MetaBuilder setProductFamily(String productFamily) {
    requireNonEmpty(productFamily, "productFamily");
    this.productFamily = productFamily;
    return this;
  }

  /**
   * Retrieves the revision.
   * 
   * @return the revision or {@code null} if one hasn't been set
   */
  public String getRevision() {
    return revision;
  }

  /**
   * Sets the revision.
   * 
   * @param revision
   *          the revision to set
   * @return the current builder instance
   */
  public MetaBuilder setRevision(String revision) {
    requireNonEmpty(revision, "revision");
    this.revision = revision;
    return this;
  }

  /**
   * Retrieves the summary.
   * 
   * @return the summary or {@code null} if one hasn't been set
   */
  public String getSummary() {
    return summary;
  }

  /**
   * Sets the summary.
   * 
   * @param summary
   *          the summary to set
   * @return the current builder instance
   */
  public MetaBuilder setSummary(String summary) {
    requireNonEmpty(summary, "summary");
    this.summary = summary;
    return this;
  }

  /**
   * Retrieves the UNSPSC code.
   * 
   * @return the unspscCode or {@code null} if one hasn't been set
   */
  public String getUnspscCode() {
    return unspscCode;
  }

  /**
   * Sets the UNSPSC code.
   * 
   * @param unspscCode
   *          the unspscCode to set
   * @return the current builder instance
   */
  public MetaBuilder setUnspscCode(String unspscCode) {
    requireNonEmpty(unspscCode, "unspscCode");
    this.unspscCode = unspscCode;
    return this;
  }

  /**
   * Retrieves the UNSPSC version.
   * 
   * @return the unspscVersion or {@code null} if one hasn't been set
   */
  public String getUnspscVersion() {
    return unspscVersion;
  }

  /**
   * Sets the UNSPSC version.
   * 
   * @param unspscVersion
   *          the unspscVersion to set
   * @return the current builder instance
   */
  public MetaBuilder setUnspscVersion(String unspscVersion) {
    requireNonEmpty(unspscVersion, "unspscVersion");
    this.unspscVersion = unspscVersion;
    return this;
  }

  @Override
  public void validate() throws ValidationException {
    super.validate();
  }
}
