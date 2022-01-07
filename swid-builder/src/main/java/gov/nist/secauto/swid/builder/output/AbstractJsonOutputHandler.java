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

package gov.nist.secauto.swid.builder.output;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import gov.nist.secauto.swid.builder.AbstractLanguageSpecificBuilder;
import gov.nist.secauto.swid.builder.EntityBuilder;
import gov.nist.secauto.swid.builder.LinkBuilder;
import gov.nist.secauto.swid.builder.MetaBuilder;
import gov.nist.secauto.swid.builder.Role;
import gov.nist.secauto.swid.builder.SWIDBuilder;
import gov.nist.secauto.swid.builder.ValidationException;
import gov.nist.secauto.swid.builder.VersionScheme;
import gov.nist.secauto.swid.builder.resource.AbstractResourceCollectionBuilder;
import gov.nist.secauto.swid.builder.resource.EvidenceBuilder;
import gov.nist.secauto.swid.builder.resource.HashAlgorithm;
import gov.nist.secauto.swid.builder.resource.PathRelativizer;
import gov.nist.secauto.swid.builder.resource.PayloadBuilder;
import gov.nist.secauto.swid.builder.resource.ResourceBuilder;
import gov.nist.secauto.swid.builder.resource.ResourceCollectionEntryGenerator;
import gov.nist.secauto.swid.builder.resource.file.AbstractFileSystemItemBuilder;
import gov.nist.secauto.swid.builder.resource.file.DirectoryBuilder;
import gov.nist.secauto.swid.builder.resource.file.FileBuilder;
import gov.nist.secauto.swid.builder.resource.firmware.FirmwareBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public abstract class AbstractJsonOutputHandler
    extends JsonSupport
    implements OutputHandler {
  /**
   * The tag identifier (text).
   */
  public static final long TAG_ID_FIELD = 0L;

  /**
   * A name (text).
   */
  public static final long SWID_NAME_FIELD = 1L;
  public static final long ENTITY_FIELD = 2L;
  public static final long EVIDENCE_FIELD = 3L;
  public static final long LINK_FIELD = 4L;
  public static final long SOFTWARE_META_FIELD = 5L;
  public static final long PAYLOAD_FIELD = 6L;
  public static final long CORPUS_FIELD = 8L;
  public static final long PATCH_FIELD = 9L;
  public static final long MEDIA_FIELD = 10L;
  public static final long SUPPLEMENTAL_FIELD = 11L;
  public static final long TAG_VERSION_FIELD = 12L;
  public static final long SOFTWARE_VERSION_FIELD = 13L;
  public static final long VERSION_SCHEME_FIELD = 14L;
  public static final long LANG_FIELD = 15L;
  public static final long DIRECTORY_FIELD = 16L;
  public static final long FILE_FIELD = 17L;
  public static final long PROCESS_FIELD = 18L;
  public static final long RESOURCE_FIELD = 19L;

  /**
   * The size of a file (number: long).
   */
  public static final long SIZE_FIELD = 20L;
  public static final long FILE_VERSION_FIELD = 21L;

  /**
   * (bool).
   */
  public static final long KEY_FIELD = 22L;
  public static final long LOCATION_FIELD = 23L;
  public static final long FS_NAME_FIELD = 24L;
  public static final long ROOT_FIELD = 25L;
  public static final long PATH_ELEMENTS_FIELD = 26L;
  public static final long PROCESS_NAME_FIELD = 27L;
  public static final long PID_FIELD = 28L;
  public static final long TYPE_FIELD = 29L;
  public static final long ENTITY_NAME_FIELD = 31L;
  public static final long REG_ID_FIELD = 32L;

  /**
   * The roles (text: space separated).
   */
  public static final long ROLE_FIELD = 33L;
  public static final long THUMBPRINT_FIELD = 34L;

  public static final long DATE_FIELD = 35L;
  public static final long DEVICE_ID_FIELD = 36L;
  public static final long ARTIFACT_FIELD = 37L;
  public static final long HREF_FIELD = 38L;
  public static final long OWNERSHIP_FIELD = 39L;
  public static final long REL_FIELD = 40L;
  public static final long MEDIA_TYPE_FIELD = 41L;
  public static final long USE_FIELD = 42L;

  public static final long ACTIVATION_STATUS_FIELD = 43L;
  public static final long CHANNEL_TYPE_FIELD = 44L;
  // colloquial-version = (45: text)
  public static final long COLLOQUIAL_VERSION_FIELD = 45L;
  // description = (46: text)
  public static final long DESCRIPTION_FIELD = 46L;
  // edition = (47: text)
  public static final long EDITION_FIELD = 47L;
  // entitlement-data-required = (48: bool)
  public static final long ENTITLEMENT_DATA_REQUIRED_FIELD = 48L;
  // entitlement-key = (49: text)
  public static final long ENTITLEMENT_KEY_FIELD = 49L;
  // generator = (50: text)
  public static final long GENERATOR_FIELD = 50L;
  // persistent-id = (51: text)
  public static final long PERSISTENT_ID_FIELD = 51L;
  // product = (52: text)
  public static final long PRODUCT_FIELD = 52L;
  // product-family = (53: text)
  public static final long PRODUCT_FAMILY_FIELD = 53L;
  // revision = (54: text)
  public static final long REVISION_FIELD = 54L;
  // summary = (55: text)
  public static final long SUMMARY_FIELD = 55L;
  // unspsc-code = (56: text)
  public static final long UNSPSC_CODE_FIELD = 56L;
  // unspsc-version = (57: text)
  public static final long UNSPSC_VERSION_FIELD = 57L;

  public static final long HASH_FIELD = 7L;

  /**
   * Firmware.
   */
  public static final long FIRMWARE_FIELD = 59L;

  private final JsonFactory jsonFactory;

  public AbstractJsonOutputHandler(JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }

  protected abstract void writeRole(JsonGenerator generator, Role role) throws IOException;

  protected abstract void writeVersionScheme(JsonGenerator generator, VersionScheme versionScheme) throws IOException;

  /**
   * @return the jsonFactory
   */
  public JsonFactory getJsonFactory() {
    return jsonFactory;
  }

  protected JsonGenerator newGenerator(OutputStream os) throws IOException {

    JsonFactory factory = getJsonFactory();

    JsonGenerator generator = factory.createGenerator(os);
    return generator;
  }

  @Override
  public void write(SWIDBuilder builder, OutputStream os) throws IOException, ValidationException {
    builder.validate();

    JsonGenerator generator = newGenerator(os);

    build(generator, builder);

    generator.close();
  }

  protected void build(JsonGenerator generator, SWIDBuilder builder) throws IOException {
    generator.writeStartObject();

    buildGlobalAttributes(generator, builder);

    // required attributes
    writeTextField(generator, TAG_ID_FIELD, builder.getTagId());

    writeIntegerField(generator, TAG_VERSION_FIELD, builder.getTagVersion());

    writeTextField(generator, SWID_NAME_FIELD, builder.getName());
    if (builder.getVersion() != null) {
      writeTextField(generator, SOFTWARE_VERSION_FIELD, builder.getVersion());
    }
    VersionScheme versionScheme = builder.getVersionScheme();
    if (versionScheme != null) {
      writeField(generator, VERSION_SCHEME_FIELD);
      writeVersionScheme(generator, versionScheme);
    }

    // optional attribute
    switch (builder.getTagType()) {
    case PRIMARY:
      break;
    case CORPUS:
      writeBooleanField(generator, CORPUS_FIELD, true);
      break;
    case PATCH:
      writeBooleanField(generator, PATCH_FIELD, true);
      break;
    case SUPPLEMENTAL:
      writeBooleanField(generator, SUPPLEMENTAL_FIELD, true);
      break;
    default:
      throw new IllegalStateException("tagType: " + builder.getTagType().toString());
    }

    // child elements
    // Required
    writeField(generator, ENTITY_FIELD);
    List<EntityBuilder> entities = builder.getEntities();
    if (entities.size() > 1) {
      generator.writeStartArray();
    }

    for (EntityBuilder entity : builder.getEntities()) {
      build(generator, entity);
    }

    if (entities.size() > 1) {
      generator.writeEndArray();
    }

    // optional
    EvidenceBuilder evidence = builder.getEvidence();
    if (evidence != null) {
      writeField(generator, EVIDENCE_FIELD);
      build(generator, evidence);
    }

    List<LinkBuilder> links = builder.getLinks();
    if (!links.isEmpty()) {
      writeField(generator, LINK_FIELD);
      if (links.size() > 1) {
        generator.writeStartArray();
      }
      for (LinkBuilder link : links) {
        build(generator, link);
      }
      if (links.size() > 1) {
        generator.writeEndArray();
      }
    }

    List<MetaBuilder> metas = builder.getMetas();
    if (!links.isEmpty()) {
      writeField(generator, SOFTWARE_META_FIELD);
      if (metas.size() > 1) {
        generator.writeStartArray();
      }
      for (MetaBuilder meta : metas) {
        buildMeta(generator, meta);
      }
      if (metas.size() > 1) {
        generator.writeEndArray();
      }
    }

    PayloadBuilder payload = builder.getPayload();
    if (payload != null) {
      writeField(generator, PAYLOAD_FIELD);
      buildPayload(generator, payload);
    }

    // TODO: media
    //
    // generator.writeStringField("test", "test");
    generator.writeEndObject();
  }

  protected void build(JsonGenerator generator, EntityBuilder builder) throws IOException {

    // start of the entity
    generator.writeStartObject();

    buildGlobalAttributes(generator, builder);

    writeTextField(generator, ENTITY_NAME_FIELD, builder.getName());
    if (builder.getRegid() != null) {
      writeTextField(generator, REG_ID_FIELD, builder.getRegid());
    }

    List<Role> roles = builder.getRoles();
    writeField(generator, ROLE_FIELD);
    if (roles.size() > 1) {
      generator.writeStartArray();
    }
    for (Role role : roles) {
      writeRole(generator, role);
    }
    if (roles.size() > 1) {
      generator.writeEndArray();
    }

    if (builder.getThumbprint() != null) {
      writeTextField(generator, THUMBPRINT_FIELD, builder.getThumbprint());
    }

    // for empty meta
    // writeField(generator, META_ELEMENTS_FIELD);
    // generator.writeStartArray();
    // generator.writeEndArray();

    // end of the entity
    generator.writeEndObject();
  }

  private void build(JsonGenerator generator, EvidenceBuilder builder) throws IOException {

    // start of the evidence
    generator.writeStartObject();

    buildGlobalAttributes(generator, builder);

    buildResourceCollection(generator, builder);

    ZonedDateTime date = builder.getDate();
    if (date != null) {
      writeDateTimeField(generator, DATE_FIELD, date);
    }

    if (builder.getDeviceId() != null) {
      writeTextField(generator, DEVICE_ID_FIELD, builder.getDeviceId());
    }

    // end of the evidence
    generator.writeEndObject();
  }

  private void build(JsonGenerator generator, LinkBuilder builder) throws IOException {

    // start of the link
    generator.writeStartObject();

    buildGlobalAttributes(generator, builder);

    // required attributes
    writeTextField(generator, HREF_FIELD, builder.getHref().toString());
    writeTextField(generator, REL_FIELD, builder.getRel());

    // optional attributes
    if (builder.getArtifact() != null) {
      writeTextField(generator, ARTIFACT_FIELD, builder.getArtifact());
    }
    if (builder.getMedia() != null) {
      writeTextField(generator, MEDIA_FIELD, builder.getMedia());
    }
    if (builder.getOwnership() != null) {
      writeTextField(generator, OWNERSHIP_FIELD, builder.getOwnership().toString());
    }
    if (builder.getMediaType() != null) {
      writeTextField(generator, MEDIA_TYPE_FIELD, builder.getMediaType());
    }
    if (builder.getUse() != null) {
      writeTextField(generator, USE_FIELD, builder.getUse().toString());
    }

    // end of the link
    generator.writeEndObject();
  }

  private void buildMeta(JsonGenerator generator, MetaBuilder builder) throws IOException {

    // start of the meta
    generator.writeStartObject();

    buildGlobalAttributes(generator, builder);

    buildAttribute(generator, ACTIVATION_STATUS_FIELD, builder.getActivationStatus());
    buildAttribute(generator, CHANNEL_TYPE_FIELD, builder.getChannelType());
    buildAttribute(generator, COLLOQUIAL_VERSION_FIELD, builder.getColloquialVersion());
    buildAttribute(generator, DESCRIPTION_FIELD, builder.getDescription());
    buildAttribute(generator, EDITION_FIELD, builder.getEdition());
    buildAttribute(generator, ENTITLEMENT_DATA_REQUIRED_FIELD, builder.getEntitlementDataRequired());
    buildAttribute(generator, ENTITLEMENT_KEY_FIELD, builder.getEntitlementKey());
    buildAttribute(generator, GENERATOR_FIELD, builder.getGenerator());
    buildAttribute(generator, PERSISTENT_ID_FIELD, builder.getPersistentId());
    buildAttribute(generator, PRODUCT_FIELD, builder.getProductBaseName());
    buildAttribute(generator, PRODUCT_FAMILY_FIELD, builder.getProductFamily());
    buildAttribute(generator, REVISION_FIELD, builder.getRevision());
    buildAttribute(generator, SUMMARY_FIELD, builder.getSummary());
    buildAttribute(generator, UNSPSC_CODE_FIELD, builder.getUnspscCode());
    buildAttribute(generator, UNSPSC_VERSION_FIELD, builder.getUnspscVersion());

    // end of the meta
    generator.writeEndObject();
  }

  private void buildAttribute(JsonGenerator generator, long fieldId, String value) throws IOException {
    if (value != null) {
      writeTextField(generator, fieldId, value);
    }
  }

  private void buildPayload(JsonGenerator generator, PayloadBuilder builder) throws IOException {

    // start of the payload
    generator.writeStartObject();

    buildGlobalAttributes(generator, builder);

    buildResourceCollection(generator, builder);

    // end of the payload
    generator.writeEndObject();
  }

  private <E extends AbstractResourceCollectionBuilder<E>> void buildResourceCollection(JsonGenerator generator,
      AbstractResourceCollectionBuilder<E> builder) throws IOException {
    buildGlobalAttributes(generator, builder);

    JsonResourceCollectionEntryGenerator creator = new JsonResourceCollectionEntryGenerator();
    {
      List<DirectoryBuilder> directories = builder.getResources(DirectoryBuilder.class);
      if (!directories.isEmpty()) {
        writeDirectories(generator, directories, creator);
      }
    }
    {
      List<FileBuilder> files = builder.getResources(FileBuilder.class);
      if (!files.isEmpty()) {
        writeFiles(generator, files, creator);
      }
    }
    {
      List<FirmwareBuilder> firmwares = builder.getResources(FirmwareBuilder.class);
      if (!firmwares.isEmpty()) {
        writeFirmware(generator, firmwares, creator);
      }
    }
  }

  private void writeDirectories(JsonGenerator generator, List<DirectoryBuilder> directories,
      JsonResourceCollectionEntryGenerator creator) throws IOException {
    writeField(generator, DIRECTORY_FIELD);
    writeResources(generator, directories, creator);
  }

  private void writeFiles(JsonGenerator generator, List<FileBuilder> files,
      JsonResourceCollectionEntryGenerator creator) throws IOException {
    writeField(generator, FILE_FIELD);
    writeResources(generator, files, creator);
  }

  private void writeFirmware(JsonGenerator generator, List<FirmwareBuilder> firmwares,
      JsonResourceCollectionEntryGenerator creator) throws IOException {
    writeField(generator, FIRMWARE_FIELD);
    writeResources(generator, firmwares, creator);
  }

  private void writeResources(JsonGenerator generator, List<? extends ResourceBuilder> resources,
      JsonResourceCollectionEntryGenerator creator) throws IOException {
    // use an array for more than one
    if (resources.size() > 1) {
      generator.writeStartArray();
    }
    for (ResourceBuilder builder : resources) {
      builder.accept(generator, creator);
    }
    if (resources.size() > 1) {
      generator.writeEndArray();
    }
  }

  private void writeHash(JsonGenerator generator, HashAlgorithm algorithm, byte[] bytes) throws IOException {
    writeField(generator, HASH_FIELD);
    generator.writeStartArray();
    generator.writeNumber(algorithm.getIndex());
    generator.writeBinary(bytes);
    generator.writeEndArray();
  }

  private <E extends AbstractLanguageSpecificBuilder<E>> void buildGlobalAttributes(JsonGenerator generator,
      AbstractLanguageSpecificBuilder<E> builder) throws IOException {
    String language = builder.getLanguage();
    if (language != null) {
      writeTextField(generator, LANG_FIELD, language);
    }
  }

  private class JsonResourceCollectionEntryGenerator implements ResourceCollectionEntryGenerator<JsonGenerator> {

    public JsonResourceCollectionEntryGenerator() {
    }

    @Override
    public void generate(JsonGenerator parent, DirectoryBuilder builder) {
      try {
        parent.writeStartObject();

        buildGlobalAttributes(parent, builder);

        buildFileSystemItem(parent, builder);

        {
          List<DirectoryBuilder> directories = builder.getResources(DirectoryBuilder.class);
          if (!directories.isEmpty()) {
            writeDirectories(parent, directories, this);
          }
        }
        {
          List<FileBuilder> files = builder.getResources(FileBuilder.class);
          if (!files.isEmpty()) {
            writeFiles(parent, files, this);
          }
        }

        parent.writeEndObject();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    @Override
    public void generate(JsonGenerator parent, FileBuilder builder) {

      try {
        parent.writeStartObject();

        buildGlobalAttributes(parent, builder);

        buildFileSystemItem(parent, builder);

        if (builder.getSize() != null) {
          writeLongField(parent, SIZE_FIELD, builder.getSize());
        }
        if (builder.getVersion() != null) {
          writeTextField(parent, SOFTWARE_VERSION_FIELD, builder.getVersion());
        }

        Map<HashAlgorithm, byte[]> hashMap = builder.getHashAlgorithmToValueMap();
        if (!hashMap.isEmpty()) {
          if (hashMap.size() > 1) {
            throw new UnsupportedOperationException("Only a single hash value is allowed");
          }

          Map.Entry<HashAlgorithm, byte[]> entry = hashMap.entrySet().iterator().next();
          writeHash(parent, entry.getKey(), entry.getValue());
        }

        // end of the payload
        parent.writeEndObject();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    @Override
    public void generate(JsonGenerator generator, FirmwareBuilder builder) {
      new CBORFirmwareOutputHandler().generate(generator, builder);
    }

    private <E extends AbstractFileSystemItemBuilder<E>> void buildFileSystemItem(JsonGenerator parent,
        AbstractFileSystemItemBuilder<E> builder) throws IOException {

      // TODO: support meta

      if (builder.getKey() != null) {
        writeBooleanField(parent, KEY_FIELD, builder.getKey());
      }

      if (builder.getRoot() != null) {
        writeTextField(parent, ROOT_FIELD, builder.getRoot());
      }

      List<String> location = builder.getLocation();
      if (location != null && !location.isEmpty()) {
        writeTextField(parent, LOCATION_FIELD, PathRelativizer.toURI(location).toString());
      }
      writeTextField(parent, FS_NAME_FIELD, builder.getName());
    }
  }
}
