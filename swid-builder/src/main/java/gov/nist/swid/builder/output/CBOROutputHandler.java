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

package gov.nist.swid.builder.output;

import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;

import gov.nist.swid.builder.AbstractLanguageSpecificBuilder;
import gov.nist.swid.builder.EntityBuilder;
import gov.nist.swid.builder.LinkBuilder;
import gov.nist.swid.builder.MetaBuilder;
import gov.nist.swid.builder.Role;
import gov.nist.swid.builder.SWIDBuilder;
import gov.nist.swid.builder.ValidationException;
import gov.nist.swid.builder.VersionScheme;
import gov.nist.swid.builder.resource.AbstractResourceCollectionBuilder;
import gov.nist.swid.builder.resource.EvidenceBuilder;
import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.PathRelativizer;
import gov.nist.swid.builder.resource.PayloadBuilder;
import gov.nist.swid.builder.resource.ResourceBuilder;
import gov.nist.swid.builder.resource.ResourceCollectionEntryGenerator;
import gov.nist.swid.builder.resource.file.AbstractFileSystemItemBuilder;
import gov.nist.swid.builder.resource.file.DirectoryBuilder;
import gov.nist.swid.builder.resource.file.FileBuilder;
import gov.nist.swid.builder.resource.firmware.FirmwareBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class CBOROutputHandler extends CBORSupport implements OutputHandler {
  /**
   * The tag identifier (text).
   */
  private static final long TAG_ID_FIELD = 0L;

  /**
   * A name (text).
   */
  private static final long SWID_NAME_FIELD = 1L;
  private static final long ENTITY_ENTRY_FIELD = 2L;
  private static final long EVIDENCE_ENTRY_FIELD = 3L;
  private static final long LINK_ENTRY_FIELD = 4L;
  private static final long SOFTWARE_META_ENTRY_FIELD = 5L;
  private static final long PAYLOAD_ENTRY_FIELD = 6L;
  private static final long CORPUS_FIELD = 8L;
  private static final long PATCH_FIELD = 9L;
  private static final long MEDIA_FIELD = 10L;
  private static final long SUPPLEMENTAL_FIELD = 11L;
  private static final long TAG_VERSION_FIELD = 12L;
  private static final long SOFTWARE_VERSION_FIELD = 13L;
  private static final long VERSION_SCHEME_FIELD = 14L;
  private static final long LANG_FIELD = 15L;
  private static final long DIRECTORY_ENTRY_FIELD = 16L;
  private static final long FILE_ENTRY_FIELD = 17L;
  /**
   * The size of a file (number: long).
   */
  private static final long SIZE_FIELD = 20L;
  private static final long FILE_VERSION_FIELD = 21L;
  /**
   * (bool).
   */
  private static final long KEY_FIELD = 22L;
  private static final long LOCATION_FIELD = 23L;
  private static final long FS_NAME_FIELD = 24L;
  private static final long ROOT_FIELD = 25L;
  private static final long PATH_ELEMENTS_FIELD = 26L;

  // private static final long META_ELEMENTS_FIELD = 29L;
  private static final long ENTITY_NAME_FIELD = 31L;
  private static final long REGID_FIELD = 32L;

  /**
   * The roles (text: space separated).
   */
  private static final long ROLE_FIELD = 33L;
  private static final long THUMBPRINT_FIELD = 34L;

  private static final long DATE_FIELD = 35L;
  private static final long DEVICE_ID_FIELD = 36L;
  private static final long ARTIFACT_FIELD = 37L;
  private static final long HREF_FIELD = 38L;
  private static final long OWNERSHIP_FIELD = 39L;
  private static final long REL_FIELD = 40L;
  private static final long MEDIA_TYPE_FIELD = 41L;
  private static final long USE_FIELD = 42L;
  private static final long TYPE_FIELD = 29L;

  private static final long ACTIVATION_STATUS_FIELD = 43L;
  private static final long CHANNEL_TYPE_FIELD = 44L;
  // colloquial-version = (45: text)
  // description = (46: text)
  // edition = (47: text)
  // entitlement-data-required = (48: bool)
  // entitlement-key = (49: text)
  // generator = (50: text)
  // persistent-id = (51: text)
  // product = (52: text)
  // product-family = (53: text)
  // revision = (54: text)
  // summary = (55: text)
  // unspsc-code = (56: text)
  // unspsc-version = (57: text)

  private static final long HASH_ENTRY_FIELD = 58L;

  /**
   * Firmware.
   */
  private static final long FIRMWARE_ENTRY_FIELD = 59L;

  public CBOROutputHandler() {
  }

  @Override
  public void write(SWIDBuilder builder, OutputStream os) throws IOException, ValidationException {
    builder.validate();

    CBORFactory factory = new CBORFactory();

    CBORGenerator generator = factory.createGenerator(os);

    build(builder, generator);

    generator.close();
  }

  protected static void build(SWIDBuilder builder, CBORGenerator generator) throws IOException {
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    // required attributes
    writeTextField(generator, TAG_ID_FIELD, builder.getTagId());

    writeIntegerField(generator, TAG_VERSION_FIELD, builder.getTagVersion());

    writeTextField(generator, SWID_NAME_FIELD, builder.getName());
    if (builder.getVersion() != null) {
      writeTextField(generator, SOFTWARE_VERSION_FIELD, builder.getVersion());
    }
    VersionScheme versionScheme = builder.getVersionScheme();
    if (versionScheme != null) {
      generator.writeFieldId(VERSION_SCHEME_FIELD);
      Integer index = versionScheme.getIndex();
      if (index != null) {
        generator.writeNumber(index);
      } else {
        generator.writeString(versionScheme.getName());
      }
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
    generator.writeFieldId(ENTITY_ENTRY_FIELD);
    List<EntityBuilder> entities = builder.getEntities();
    if (entities.size() > 1) {
      generator.writeStartArray();
    }

    for (EntityBuilder entity : builder.getEntities()) {
      build(entity, generator);
    }

    if (entities.size() > 1) {
      generator.writeEndArray();
    }

    // optional
    EvidenceBuilder evidence = builder.getEvidence();
    if (evidence != null) {
      generator.writeFieldId(EVIDENCE_ENTRY_FIELD);
      build(evidence, generator);
    }

    List<LinkBuilder> links = builder.getLinks();
    if (!links.isEmpty()) {
      generator.writeFieldId(LINK_ENTRY_FIELD);
      if (links.size() > 1) {
        generator.writeStartArray();
      }
      for (LinkBuilder link : links) {
        build(link, generator);
      }
      if (links.size() > 1) {
        generator.writeEndArray();
      }
    }

    List<MetaBuilder> metas = builder.getMetas();
    if (!links.isEmpty()) {
      generator.writeFieldId(SOFTWARE_META_ENTRY_FIELD);
      if (metas.size() > 1) {
        generator.writeStartArray();
      }
      for (MetaBuilder meta : metas) {
        build(meta, generator);
      }
      if (metas.size() > 1) {
        generator.writeEndArray();
      }
    }

    PayloadBuilder payload = builder.getPayload();
    if (payload != null) {
      generator.writeFieldId(PAYLOAD_ENTRY_FIELD);
      build(payload, generator);
    }

    // TODO: media
    //
    // generator.writeStringField("test", "test");
    generator.writeEndObject();
  }

  protected static void build(EntityBuilder builder, CBORGenerator generator) throws IOException {

    // start of the entity
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    writeTextField(generator, ENTITY_NAME_FIELD, builder.getName());
    if (builder.getRegid() != null) {
      writeTextField(generator, REGID_FIELD, builder.getRegid());
    }

    List<Role> roles = builder.getRoles();
    generator.writeFieldId(ROLE_FIELD);
    if (roles.size() > 1) {
      generator.writeStartArray();
    }
    for (Role role : roles) {
      Integer index = role.getIndex();
      if (index != null) {
        generator.writeNumber(index);
      } else {
        generator.writeString(role.getName());
      }
    }
    if (roles.size() > 1) {
      generator.writeEndArray();
    }

    if (builder.getThumbprint() != null) {
      writeTextField(generator, THUMBPRINT_FIELD, builder.getThumbprint());
    }

    // for empty meta
    // generator.writeFieldId(META_ELEMENTS_FIELD);
    // generator.writeStartArray();
    // generator.writeEndArray();

    // end of the entity
    generator.writeEndObject();
  }

  private static void build(EvidenceBuilder builder, CBORGenerator generator) throws IOException {

    // start of the evidence
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    buildResourceCollection(builder, generator);

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

  private static void build(LinkBuilder builder, CBORGenerator generator) throws IOException {

    // start of the link
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

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

  private static void build(MetaBuilder builder, CBORGenerator generator) throws IOException {

    // start of the meta
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    // TODO Auto-generated method stub

    // end of the meta
    generator.writeEndObject();
  }

  private static void build(PayloadBuilder builder, CBORGenerator generator) throws IOException {

    // start of the payload
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    buildResourceCollection(builder, generator);

    // end of the payload
    generator.writeEndObject();
  }

  private static <E extends AbstractResourceCollectionBuilder<E>> void buildResourceCollection(
      AbstractResourceCollectionBuilder<E> builder, CBORGenerator generator) throws IOException {
    buildGlobalAttributes(builder, generator);

    CBORResourceCollectionEntryGenerator creator = new CBORResourceCollectionEntryGenerator();
    {
      List<DirectoryBuilder> directories = builder.getResources(DirectoryBuilder.class);
      if (!directories.isEmpty()) {
        writeDirectories(directories, generator, creator);
      }
    }
    {
      List<FileBuilder> files = builder.getResources(FileBuilder.class);
      if (!files.isEmpty()) {
        writeFiles(files, generator, creator);
      }
    }
    {
      List<FirmwareBuilder> firmwares = builder.getResources(FirmwareBuilder.class);
      if (!firmwares.isEmpty()) {
        writeFirmware(firmwares, generator, creator);
      }
    }
  }

  private static void writeDirectories(List<DirectoryBuilder> directories, CBORGenerator generator,
      CBORResourceCollectionEntryGenerator creator) throws IOException {
    generator.writeFieldId(DIRECTORY_ENTRY_FIELD);
    writeResources(directories, generator, creator);
  }

  private static void writeFiles(List<FileBuilder> files, CBORGenerator generator,
      CBORResourceCollectionEntryGenerator creator) throws IOException {
    generator.writeFieldId(FILE_ENTRY_FIELD);
    writeResources(files, generator, creator);
  }

  private static void writeFirmware(List<FirmwareBuilder> firmwares, CBORGenerator generator,
      CBORResourceCollectionEntryGenerator creator) throws IOException {
    generator.writeFieldId(FIRMWARE_ENTRY_FIELD);
    writeResources(firmwares, generator, creator);
  }

  private static void writeResources(List<? extends ResourceBuilder> resources, CBORGenerator generator,
      CBORResourceCollectionEntryGenerator creator) throws IOException {
    // use an array for more than one
    if (resources.size() > 1) {
      generator.writeStartArray();
    }
    for (ResourceBuilder builder : resources) {
      builder.accept(creator, generator);
    }
    if (resources.size() > 1) {
      generator.writeEndArray();
    }
  }

  private static void writeHash(HashAlgorithm algorithm, byte[] bytes, CBORGenerator generator) throws IOException {
    generator.writeFieldId(HASH_ENTRY_FIELD);
    generator.writeStartArray();
    generator.writeNumber(algorithm.getIndex());
    generator.writeBinary(bytes);
    generator.writeEndArray();
  }

  private static <E extends AbstractLanguageSpecificBuilder<E>> void buildGlobalAttributes(
      AbstractLanguageSpecificBuilder<E> builder, CBORGenerator generator) throws IOException {
    String language = builder.getLanguage();
    if (language != null) {
      writeTextField(generator, LANG_FIELD, language);
    }
  }


  public static class CBORResourceCollectionEntryGenerator implements ResourceCollectionEntryGenerator<CBORGenerator> {

    public CBORResourceCollectionEntryGenerator() {
    }

    @Override
    public void generate(DirectoryBuilder builder, CBORGenerator parent) {
      try {
        parent.writeStartObject();

        buildGlobalAttributes(builder, parent);

        buildFileSystemItem(builder, parent);

        {
          List<DirectoryBuilder> directories = builder.getResources(DirectoryBuilder.class);
          if (!directories.isEmpty()) {
            writeDirectories(directories, parent, this);
          }
        }
        {
          List<FileBuilder> files = builder.getResources(FileBuilder.class);
          if (!files.isEmpty()) {
            writeFiles(files, parent, this);
          }
        }

        parent.writeEndObject();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    @Override
    public void generate(FileBuilder builder, CBORGenerator parent) {

      try {
        parent.writeStartObject();

        buildGlobalAttributes(builder, parent);

        buildFileSystemItem(builder, parent);

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
          writeHash(entry.getKey(), entry.getValue(), parent);
        }

        // end of the payload
        parent.writeEndObject();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    @Override
    public void generate(FirmwareBuilder builder, CBORGenerator generator) {
        new CBORFirmwareOutputHandler().generate(builder, generator);
    }

    private <E extends AbstractFileSystemItemBuilder<E>> void buildFileSystemItem(
        AbstractFileSystemItemBuilder<E> builder, CBORGenerator parent) throws IOException {

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
