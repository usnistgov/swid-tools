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

package gov.nist.swid.builder.output;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;

import gov.nist.swid.builder.AbstractBuilder;
import gov.nist.swid.builder.AbstractFileSystemItemBuilder;
import gov.nist.swid.builder.AbstractResourceCollectionBuilder;
import gov.nist.swid.builder.EntityBuilder;
import gov.nist.swid.builder.EvidenceBuilder;
import gov.nist.swid.builder.FileBuilder;
import gov.nist.swid.builder.LinkBuilder;
import gov.nist.swid.builder.MetaBuilder;
import gov.nist.swid.builder.PayloadBuilder;
import gov.nist.swid.builder.ResourceBuilder;
import gov.nist.swid.builder.ResourceCollectionEntryGenerator;
import gov.nist.swid.builder.SWIDBuilder;
import gov.nist.swid.builder.resource.PathRelativizer;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CBOROutputHandler implements OutputHandler {
  /**
   * The tag identifier (text).
   */
  private static final long TAG_ID_FIELD = 0L;
  /**
   * A name (text).
   */
  private static final long NAME_FIELD = 1L;
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
  private static final long VERSION_FIELD = 13L;
  private static final long VERSION_SCHEME_FIELD = 14L;
  private static final long LANG_FIELD = 15L;
  // private static final long DIRECTORY_ENTRY_FIELD = 16L;
  private static final long FILE_ENTRY_FIELD = 17L;
  /**
   * The size of a file (number: long).
   */
  private static final long SIZE_FIELD = 20L;
  /**
   * (bool).
   */
  private static final long KEY_FIELD = 21L;
  private static final long LOCATION_FIELD = 22L;
  private static final long ROOT_FIELD = 23L;
  private static final long MEDIA_TYPE_FIELD = 26L;

  /**
   * The roles (text: space separated).
   */
  private static final long ROLE_FIELD = 29L;
  private static final long THUMBPRINT_FIELD = 30L;
  private static final long DATE_FIELD = 31L;
  private static final long DEVICE_ID_FIELD = 32L;
  private static final long ARTIFACT_FIELD = 33L;
  private static final long HREF_FIELD = 34L;
  private static final long OWNERSHIP_FIELD = 35L;
  private static final long REL_FIELD = 36L;
  private static final long USE_FIELD = 37L;

  public CBOROutputHandler() {
  }

  @Override
  public void write(SWIDBuilder builder, OutputStream os) throws IOException {

    CBORFactory factory = new CBORFactory();

    CBORGenerator generator = factory.createGenerator(os, JsonEncoding.UTF8);

    build(builder, generator);

    generator.close();
  }

  protected void build(SWIDBuilder builder, CBORGenerator generator) throws IOException {

    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    // required attributes
    writeTextField(generator, NAME_FIELD, builder.getName());
    writeTextField(generator, TAG_ID_FIELD, builder.getTagId());

    // child elements
    // Required
    for (EntityBuilder entity : builder.getEntities()) {
      build(entity, generator);
    }

    // optional
    EvidenceBuilder evidence = builder.getEvidence();
    if (evidence != null) {
      build(evidence, generator);
    }

    for (LinkBuilder link : builder.getLinks()) {
      build(link, generator);
    }

    for (MetaBuilder meta : builder.getMetas()) {
      build(meta, generator);
    }

    PayloadBuilder payload = builder.getPayload();
    if (payload != null) {
      build(payload, generator);
    }

    // optional attributes
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

    // TODO: media

    writeIntegerField(generator, TAG_VERSION_FIELD, builder.getTagVersion());

    if (builder.getVersion() != null) {
      writeTextField(generator, VERSION_FIELD, builder.getVersion());
    }
    if (builder.getVersionScheme() != null) {
      writeTextField(generator, VERSION_SCHEME_FIELD, builder.getVersionScheme());
    }

    generator.writeStringField("test", "test");
  }

  protected void build(EntityBuilder builder, CBORGenerator generator) throws IOException {

    // start of the entity
    generator.writeFieldId(ENTITY_ENTRY_FIELD);
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    writeTextField(generator, NAME_FIELD, builder.getName());
    if (builder.getRegid() != null) {
      writeTextField(generator, NAME_FIELD, builder.getRegid());
    }

    StringBuilder sb = null;
    for (String role : builder.getRoles()) {
      if (sb == null) {
        sb = new StringBuilder();
      } else {
        sb.append(' ');
      }
      sb.append(role);
    }
    writeTextField(generator, ROLE_FIELD, sb.toString());

    if (builder.getThumbprint() != null) {
      writeTextField(generator, THUMBPRINT_FIELD, builder.getThumbprint());
    }

    // end of the entity
    generator.writeEndObject();
  }

  private void build(EvidenceBuilder builder, CBORGenerator generator) throws IOException {

    // start of the evidence
    generator.writeFieldId(EVIDENCE_ENTRY_FIELD);
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

  private void build(LinkBuilder builder, CBORGenerator generator) throws IOException {

    // start of the link
    generator.writeFieldId(LINK_ENTRY_FIELD);
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

  private void build(MetaBuilder builder, CBORGenerator generator) throws IOException {

    // start of the meta
    generator.writeFieldId(SOFTWARE_META_ENTRY_FIELD);
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    // TODO Auto-generated method stub

    // end of the meta
    generator.writeEndObject();
  }

  private void build(PayloadBuilder builder, CBORGenerator generator) throws IOException {

    // start of the payload
    generator.writeFieldId(PAYLOAD_ENTRY_FIELD);
    generator.writeStartObject();

    buildGlobalAttributes(builder, generator);

    buildResourceCollection(builder, generator);

    // end of the payload
    generator.writeEndObject();
  }

  private static <E extends AbstractResourceCollectionBuilder<E>> void buildResourceCollection(
      AbstractResourceCollectionBuilder<E> builder, CBORGenerator generator) throws IOException {
    buildGlobalAttributes(builder, generator);

    ResourceCollectionEntryGenerator creator = new CBORResourceCollectionEntryGenerator(generator);
    for (ResourceBuilder resourceBuilder : builder.getResources()) {
      try {
        resourceBuilder.accept(creator);
      } catch (RuntimeException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof IOException) {
          throw (IOException) cause;
        } else {
          throw ex;
        }
      }
    }
  }

  private static <E extends AbstractBuilder<E>> void buildGlobalAttributes(AbstractBuilder<E> builder,
      CBORGenerator generator) throws IOException {
    String language = builder.getLanguage();
    if (language != null) {
      writeTextField(generator, LANG_FIELD, language);
    }
  }

  private static void writeTextField(CBORGenerator generator, long fieldId, String text) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeString(text);
  }

  private static void writeBooleanField(CBORGenerator generator, long fieldId, boolean state) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeBoolean(state);

  }

  private static void writeIntegerField(CBORGenerator generator, long fieldId, int value) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeNumber(value);
  }

  private static void writeLongField(CBORGenerator generator, long fieldId, long value) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeNumber(value);
  }

  private void writeDateTimeField(CBORGenerator generator, long fieldId, ZonedDateTime dateTime) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeString(dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
  }

  public static class CBORResourceCollectionEntryGenerator implements ResourceCollectionEntryGenerator {

    private final CBORGenerator generator;

    public CBORResourceCollectionEntryGenerator(CBORGenerator generator) {
      this.generator = generator;
    }

    @Override
    public void generate(FileBuilder builder) {

      try {
        // start of the payload
        generator.writeFieldId(FILE_ENTRY_FIELD);
        generator.writeStartObject();

        buildGlobalAttributes(builder, generator);

        buildFileSystemItem(builder);

        if (builder.getSize() != null) {
          writeLongField(generator, SIZE_FIELD, builder.getSize());
        }
        if (builder.getVersion() != null) {
          writeTextField(generator, VERSION_FIELD, builder.getVersion());
        }
        //
        // for (Map.Entry<HashAlgorithm, String> entry :
        // builder.getHashAlgorithmToValueMap().entrySet()) {
        // HashAlgorithm algorithm = entry.getKey();
        // String hashValue = entry.getValue();
        // Namespace ns = Namespace.getNamespace(algorithm.getName(), algorithm.getNamespace());
        // Namespace nsOld = parent.getNamespace(ns.getPrefix());
        //
        // if (nsOld == null) {
        // parent.addNamespaceDeclaration(ns);
        // } else if (!nsOld.getURI().equals(ns.getURI())) {
        // element.addNamespaceDeclaration(ns);
        // }
        // element.setAttribute("hash", hashValue, ns);
        // }

        // end of the payload
        generator.writeEndObject();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    private <E extends AbstractFileSystemItemBuilder<E>> void buildFileSystemItem(
        AbstractFileSystemItemBuilder<E> builder) throws IOException {

      // TODO: support meta

      if (builder.getKey() != null) {
        writeBooleanField(generator, KEY_FIELD, builder.getKey());
      }

      if (builder.getRoot() != null) {
        writeTextField(generator, ROOT_FIELD, builder.getRoot());
      }

      List<String> location = builder.getLocation();
      if (location != null && !location.isEmpty()) {
        writeTextField(generator, LOCATION_FIELD, PathRelativizer.toURI(location).toString());
      }
      writeTextField(generator, NAME_FIELD, builder.getName());
    }

  }
}
