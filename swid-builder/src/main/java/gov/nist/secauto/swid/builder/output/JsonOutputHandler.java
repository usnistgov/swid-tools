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

import gov.nist.secauto.swid.builder.Role;
import gov.nist.secauto.swid.builder.VersionScheme;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonOutputHandler
    extends AbstractJsonOutputHandler {
  private static final Map<Long, String> fieldIdToNameMap;

  static {
    fieldIdToNameMap = new HashMap<>();
    fieldIdToNameMap.put(TAG_ID_FIELD, "tag-id");
    fieldIdToNameMap.put(SWID_NAME_FIELD, "swid-name");
    fieldIdToNameMap.put(ENTITY_FIELD, "entity");
    fieldIdToNameMap.put(EVIDENCE_FIELD, "evidence");
    fieldIdToNameMap.put(LINK_FIELD, "link");
    fieldIdToNameMap.put(SOFTWARE_META_FIELD, "software-meta");
    fieldIdToNameMap.put(PAYLOAD_FIELD, "payload");
    fieldIdToNameMap.put(HASH_FIELD, "hash");
    fieldIdToNameMap.put(CORPUS_FIELD, "corpus");
    fieldIdToNameMap.put(PATCH_FIELD, "patch");
    fieldIdToNameMap.put(MEDIA_FIELD, "media");
    fieldIdToNameMap.put(SUPPLEMENTAL_FIELD, "supplemental");
    fieldIdToNameMap.put(TAG_VERSION_FIELD, "tag-version");
    fieldIdToNameMap.put(SOFTWARE_VERSION_FIELD, "software-version");
    fieldIdToNameMap.put(VERSION_SCHEME_FIELD, "version-scheme");
    fieldIdToNameMap.put(LANG_FIELD, "lang");
    fieldIdToNameMap.put(DIRECTORY_FIELD, "directory");
    fieldIdToNameMap.put(FILE_FIELD, "file");
    fieldIdToNameMap.put(PROCESS_FIELD, "process");
    fieldIdToNameMap.put(RESOURCE_FIELD, "resource");
    fieldIdToNameMap.put(SIZE_FIELD, "size");
    fieldIdToNameMap.put(FILE_VERSION_FIELD, "file-version");
    fieldIdToNameMap.put(KEY_FIELD, "key");
    fieldIdToNameMap.put(LOCATION_FIELD, "location");
    fieldIdToNameMap.put(FS_NAME_FIELD, "fs-name");
    fieldIdToNameMap.put(ROOT_FIELD, "root");
    fieldIdToNameMap.put(PATH_ELEMENTS_FIELD, "path-elements");
    fieldIdToNameMap.put(PROCESS_NAME_FIELD, "process-name");
    fieldIdToNameMap.put(PID_FIELD, "pid");
    fieldIdToNameMap.put(TYPE_FIELD, "type");
    fieldIdToNameMap.put(ENTITY_NAME_FIELD, "entity-name");
    fieldIdToNameMap.put(REG_ID_FIELD, "reg-id");
    fieldIdToNameMap.put(ROLE_FIELD, "role");
    fieldIdToNameMap.put(THUMBPRINT_FIELD, "thumbprint");
    fieldIdToNameMap.put(DATE_FIELD, "date");
    fieldIdToNameMap.put(DEVICE_ID_FIELD, "device-id");
    fieldIdToNameMap.put(ARTIFACT_FIELD, "artifact");
    fieldIdToNameMap.put(HREF_FIELD, "href");
    fieldIdToNameMap.put(OWNERSHIP_FIELD, "ownership");
    fieldIdToNameMap.put(REL_FIELD, "rel");
    fieldIdToNameMap.put(MEDIA_TYPE_FIELD, "media-type");
    fieldIdToNameMap.put(USE_FIELD, "use");
    fieldIdToNameMap.put(ACTIVATION_STATUS_FIELD, "activation-status");
    fieldIdToNameMap.put(CHANNEL_TYPE_FIELD, "channel-type");
    fieldIdToNameMap.put(COLLOQUIAL_VERSION_FIELD, "colloquial-version");
    fieldIdToNameMap.put(DESCRIPTION_FIELD, "description");
    fieldIdToNameMap.put(EDITION_FIELD, "edition");
    fieldIdToNameMap.put(ENTITLEMENT_DATA_REQUIRED_FIELD, "entitlement-data-required");
    fieldIdToNameMap.put(ENTITLEMENT_KEY_FIELD, "entitlement-key");
    fieldIdToNameMap.put(GENERATOR_FIELD, "generator");
    fieldIdToNameMap.put(PERSISTENT_ID_FIELD, "persistent-id");
    fieldIdToNameMap.put(PRODUCT_FIELD, "product");
    fieldIdToNameMap.put(PRODUCT_FAMILY_FIELD, "product-family");
    fieldIdToNameMap.put(REVISION_FIELD, "revision");
    fieldIdToNameMap.put(SUMMARY_FIELD, "summary");
    fieldIdToNameMap.put(UNSPSC_CODE_FIELD, "unspsc-code");
    fieldIdToNameMap.put(UNSPSC_VERSION_FIELD, "unspsc-version");
  }

  private static String lookupFieldName(long fieldId) {
    return fieldIdToNameMap.get(fieldId);
  }

  public JsonOutputHandler() {
    this(new JsonFactory());
  }

  public JsonOutputHandler(JsonFactory jsonFactory) {
    super(jsonFactory);
  }

  @Override
  protected void writeField(JsonGenerator generator, long fieldId) throws IOException {
    generator.writeFieldName(lookupFieldName(fieldId));
  }

  @Override
  protected void writeRole(JsonGenerator generator, Role role) throws IOException {
    generator.writeString(role.getName());
  }

  @Override
  protected void writeVersionScheme(JsonGenerator generator, VersionScheme versionScheme) throws IOException {
    generator.writeString(versionScheme.getName());
  }
}
