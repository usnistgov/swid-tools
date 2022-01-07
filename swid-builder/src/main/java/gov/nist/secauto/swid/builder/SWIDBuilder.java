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

import gov.nist.secauto.swid.builder.resource.EvidenceBuilder;
import gov.nist.secauto.swid.builder.resource.PayloadBuilder;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SWIDBuilder
    extends AbstractLanguageSpecificBuilder<SWIDBuilder> {
  private TagType tagType = TagType.PRIMARY;
  private String name;
  private String tagId;
  private BigInteger tagVersion = SWIDConstants.TAG_VERSION_DEFAULT;
  private String version;
  private VersionScheme versionScheme;
  private List<EntityBuilder> entities = new LinkedList<>();
  private EvidenceBuilder evidence;
  private List<LinkBuilder> links = new LinkedList<>();
  private List<MetaBuilder> metas = new LinkedList<>();
  private PayloadBuilder payload;
  private String media;

  protected SWIDBuilder() {
    super();
  }

  @Override
  public void reset() {
    super.reset();

    language(Locale.getDefault().toLanguageTag());

    this.tagType = TagType.PRIMARY;
    this.name = null;
    this.tagId = null;
    this.tagVersion = SWIDConstants.TAG_VERSION_DEFAULT;
    this.version = null;
    this.versionScheme = null;
    this.entities = new LinkedList<>();
    ;
    this.evidence = null;
    this.links = new LinkedList<>();
    ;
    this.metas = new LinkedList<>();
    ;
    this.payload = null;
    this.media = null;
  }

  public static SWIDBuilder create() {
    return new SWIDBuilder();
  }

  public String getTagId() {
    return tagId;
  }

  public TagType getTagType() {
    return tagType;
  }

  public String getName() {
    return name;
  }

  public BigInteger getTagVersion() {
    return tagVersion;
  }

  public String getVersion() {
    return (version == null ? SWIDConstants.VERSION_DEFAULT : version);
  }

  public VersionScheme getVersionScheme() {
    return (versionScheme == null ? SWIDConstants.VERSION_SCHEME_DEFAULT : versionScheme);
  }

  public List<EntityBuilder> getEntities() {
    return entities;
  }

  public EvidenceBuilder getEvidence() {
    return evidence;
  }

  public PayloadBuilder getPayload() {
    return payload;
  }

  /**
   * Provide a new evidence node if a previous evidence node was not provided, or the cached node if
   * one already exists.
   * 
   * @return the evidence node builder instance
   */
  public EvidenceBuilder newEvidence() {
    if (evidence == null) {
      evidence = EvidenceBuilder.create();
    }
    return evidence;
  }

  public List<LinkBuilder> getLinks() {
    return links;
  }

  public List<MetaBuilder> getMetas() {
    return metas;
  }

  /**
   * Retrieves the existing PayloadBuilder or creates a new one if no PayloadBuilder has been created
   * already.
   * 
   * @return the payload builder
   */
  public PayloadBuilder newPayload() {
    if (payload == null) {
      payload = PayloadBuilder.create();
    }
    return payload;
  }

  public String getMedia() {
    return media;
  }

  /**
   * Sets the to-be-built tag's product type to the provided value.
   * 
   * @param type
   *          the new type to set
   * @return the same builder instance
   */
  public SWIDBuilder tagType(TagType type) {
    Objects.requireNonNull(type, "tagType");
    this.tagType = type;
    return this;
  }

  /**
   * Sets the to-be-built tag's product name to the provided value.
   * 
   * @param name
   *          the name of the software product
   * @return the same builder instance
   */
  public SWIDBuilder name(String name) {
    requireNonEmpty(name, "name");
    this.name = name;
    return this;
  }

  /**
   * Sets the to-be-built tag's product tag identifier to the provided value.
   * 
   * @param id
   *          the tag identifier for the software product
   * @return the same builder instance
   */
  public SWIDBuilder tagId(String id) {
    requireNonEmpty(id, "id");
    this.tagId = id;
    return this;
  }

  public SWIDBuilder tagVersion(long version) {
    return tagVersion(BigInteger.valueOf(version));
  }

  /**
   * Set the tag's tag version.
   * 
   * @param version
   *          the version value to use
   * @return the same builder instance
   */
  public SWIDBuilder tagVersion(BigInteger version) {
    Objects.requireNonNull(version, "tagVersion");
    this.tagVersion = version;
    return this;
  }

  /**
   * Sets the to-be-built SWID tag's version to the provided value.
   * 
   * @param version
   *          the version value to use
   * @return the same builder instance
   */
  public SWIDBuilder version(String version) {
    requireNonEmpty(version, "version");
    if (SWIDConstants.VERSION_DEFAULT.equals(version)) {
      this.version = null;
    } else {
      this.version = version;
    }
    return this;
  }

  /**
   * Sets the to-be-built SWID tag's versionSchema to the provided value. The version scheme
   * identifies the structure of the provided version.
   * 
   * @see VersionScheme#lookupByIndex(int)
   * @see VersionScheme#lookupByName(String)
   * @see VersionScheme#assignPrivateVersionScheme(int, String)
   * @param scheme
   *          the version scheme for the tag
   * @return the same builder instance
   * @see #version(String)
   */
  public SWIDBuilder versionScheme(VersionScheme scheme) {
    Objects.requireNonNull(scheme, "versionScheme");
    this.versionScheme = scheme;
    return this;
  }

  /**
   * Sets the to-be-built SWID tag's media to the provided value.
   * 
   * @param media
   *          the media value to use
   * @return the same builder instance
   */
  public SWIDBuilder media(String media) {
    Objects.requireNonNull(media, "media");
    this.media = media;
    return this;
  }

  /**
   * Adds a new entity to the tag.
   * 
   * @param entity
   *          a entity builder representing the new entity to add
   * @return the same builder instance
   */
  public SWIDBuilder addEntity(EntityBuilder entity) {
    Objects.requireNonNull(entity, "entity");
    this.entities.add(entity);
    return this;
  }

  /**
   * Adds a new link to the tag.
   * 
   * @param link
   *          a link builder representing the new link to add
   * @return the same builder instance
   */
  public SWIDBuilder addLink(LinkBuilder link) {
    Objects.requireNonNull(link, "link");
    this.links.add(link);
    return this;
  }

  /**
   * Adds a new meta to the tag.
   * 
   * @param meta
   *          a meta builder representing the new meta to add
   * @return the same builder instance
   */
  public SWIDBuilder addMeta(MetaBuilder meta) {
    Objects.requireNonNull(meta, "meta");
    this.metas.add(meta);
    return this;
  }

  /**
   * Adds a new payload to the tag.
   * 
   * @param payload
   *          a payload builder representing the new payload to add
   * @return the same builder instance
   */
  public SWIDBuilder payload(PayloadBuilder payload) {
    Objects.requireNonNull(payload, "payload");
    this.payload = payload;
    return this;
  }

  /**
   * Adds a new evidence to the tag.
   * 
   * @param evidence
   *          a evidence builder representing the new evidence to add
   * @return the same builder instance
   */
  public SWIDBuilder evidence(EvidenceBuilder evidence) {
    Objects.requireNonNull(evidence, "evidence");
    this.evidence = evidence;
    return this;
  }

  @Override
  public void validate() throws ValidationException {
    super.validate();
    validateNonEmpty("name", name);
    validateNonEmpty("tagId", tagId);
    validateNonEmpty("entity", entities);
    boolean foundTagCreator = false;
    for (EntityBuilder entity : entities) {
      entity.validate();
      if (entity.getRoles().contains(KnownRole.TAG_CREATOR)) {
        foundTagCreator = true;
      }
    }
    if (!foundTagCreator) {
      throw new ValidationException(
          "at least one entity wwith the role '" + KnownRole.TAG_CREATOR.getName() + "' must be provided");
    }

    if (payload != null && evidence != null) {
      throw new ValidationException("Only one of evidence or payload must be provided");
    }

    if (payload != null) {
      payload.validate();
    }

    if (evidence != null) {
      evidence.validate();
    }

    if (!links.isEmpty()) {
      for (LinkBuilder link : links) {
        link.validate();
      }
    }

    if (!metas.isEmpty()) {
      for (MetaBuilder meta : metas) {
        meta.validate();
      }
    }
  }

}
