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

import com.fasterxml.jackson.core.JsonGenerator;

import gov.nist.secauto.swid.builder.resource.firmware.DeviceIdentifier;
import gov.nist.secauto.swid.builder.resource.firmware.FirmwareBuilder;
import gov.nist.secauto.swid.builder.resource.firmware.FirmwarePayloadBuilder;
import gov.nist.secauto.swid.builder.resource.firmware.FirmwarePayloadDigest;
import gov.nist.secauto.swid.builder.resource.firmware.FirmwarePayloadPackage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CBORFirmwareOutputHandler
    extends JsonSupport {
  /**
   * Firmware.
   */
  private static final long FIRMWARE_MANIFEST_ID_FIELD = 63L; // bytes / text / uint
  private static final long FIRMWARE_MANIFEST_CREATION_TIMESTAMP = 64L; // time
  private static final long FIRMWARE_MANIFEST_VERSION = 65L; // uint
  private static final long FIRMWARE_MANIFEST_DESCRIPTION = 66L; // text
  private static final long FIRMWARE_MANIFEST_NONCE = 67L; // bytes
  // private static final long FIRMWARE_MANIFEST_DEPENDENCIES = 68L; //
  // resource-reference
  // private static final long FIRMWARE_MANIFEST_ALIASES = 69L; //
  // resource-reference
  private static final long FIRMWARE_MANIFEST_TARGET_DEVICE_ID = 85L; // object
  // firmware-payload / [ 2* firmware-payload ]
  private static final long FIRMWARE_MANIFEST_PAYLOAD_ENTRY = 60L;
  private static final long FIRMWARE_MANIFEST_SIMPLE_EXTENSIONS = 115L; // { + int => bytes }

  private static final long FIRMWARE_PAYLOAD_ID = 61L; // bytes / text / uint
  private static final long FIRMWARE_PAYLOAD_PACKAGE_ID = 62L; // text
  // private static final long FIRMWARE_PAYLOAD_DESCRIPTION = 70L; // text
  private static final long FIRMWARE_PAYLOAD_FORMAT = 71L; // object
  private static final long FIRMWARE_PAYLOAD_SIZE = 74L; // unint
  // private static final long FIRMWARE_PAYLOAD_SIMPLE_VERSION = 107L; // uint
  // private static final long FIRMWARE_PAYLOAD_VERSION = 108L; // object
  private static final long FIRMWARE_PAYLOAD_DIGESTS = 97L; // object
  // private static final long FIRMWARE_PAYLOAD_TARGET_COMPONENT_INDEX = 79L; //
  // text
  private static final long FIRMWARE_PAYLOAD_STORAGE_IDENTIFIER = 80L; // bytes / text / uint
  // private static final long FIRMWARE_PAYLOAD_CONDITIONS = 101L; // array of
  // objects
  // private static final long FIRMWARE_PAYLOAD_DIRECTIVES = 104L; // array of
  // objects
  // private static final long FIRMWARE_PAYLOAD_TARGET_DEPENDENCY = 81L; // array
  // of objects
  // private static final long FIRMWARE_PAYLOAD_TARGET_MINIMAL_VERSION = 92L; //
  // object
  // private static final long FIRMWARE_PAYLOAD_RELATIONSHIPS = 84L; // enum (int)
  private static final long FIRMWARE_PAYLOAD_PACKAGE = 75L; // object
  // private static final long FIRMWARE_PAYLOAD_SIMPLE_EXTENSIONS = 116L; // { +
  // int => bytes }

  /*
   * Device Identifier object
   */
  private static final long FIRMWARE_TARGET_DEVICE_IDENTIFIER_VENDOR = 86L; // text
  private static final long FIRMWARE_TARGET_DEVICE_IDENTIFIER_TYPE = 87L; // text
  private static final long FIRMWARE_TARGET_DEVICE_IDENTIFIER_MODEL = 88L; // text
  private static final long FIRMWARE_TARGET_DEVICE_IDENTIFIER_CLASS = 89L; // text
  private static final long FIRMWARE_TARGET_DEVICE_IDENTIFIER_RFC4122 = 90L; // text
  private static final long FIRMWARE_TARGET_DEVICE_IDENTIFIER_IEEE802_1_AR = 91L; // bytes

  /*
   * Payload Format object
   */
  private static final long FIRMWARE_PAYLOAD_FORMAT_TYPE = 72L; // int
  private static final long FIRMWARE_PAYLOAD_FORMAT_GUIDANCE = 73L; // bytes

  /*
   * Payload Digest object
   */
  private static final long FIRMWARE_PAYLOAD_DIGEST_TYPE = 98L; // enum (int)
  private static final long FIRMWARE_PAYLOAD_DIGEST_GUIDANCE = 99L; // bytes
  private static final long FIRMWARE_PAYLOAD_DIGEST_VALUE = 100L; // bytes

  /*
   * Firmware Package object (75)
   */
  private static final long FIRMWARE_PAYLOAD_PACKAGE_COMPRESSION_TYPE = 76L; // text / int
  private static final long FIRMWARE_PAYLOAD_PACKAGE_COMPRESSION_GUIDANCE = 77L; // bytes
  private static final long FIRMWARE_PAYLOAD_PACKAGE_VALUE = 78L; // bytes

  /**
   * Generate a CBOR object based on the provided builder.
   * 
   * @param generator
   *          the generator to write data to
   * @param builder
   *          the firmware builder to read data from
   */
  public void generate(JsonGenerator generator, FirmwareBuilder builder) {
    try {
      generator.writeStartObject();

      writeField(generator, FIRMWARE_MANIFEST_ID_FIELD, builder.getId());

      writeDateTimeField(generator, FIRMWARE_MANIFEST_CREATION_TIMESTAMP, builder.getCreationTimestamp());

      writeIntegerField(generator, FIRMWARE_MANIFEST_VERSION, builder.getVersion());

      if (builder.getDescription() != null) {
        writeTextField(generator, FIRMWARE_MANIFEST_DESCRIPTION, builder.getDescription());
      }

      writeBinaryField(generator, FIRMWARE_MANIFEST_NONCE, builder.getNonce());

      // FIRMWARE_MANIFEST_DEPENDENCIES
      // FIRMWARE_MANIFEST_ALIASES

      writeDeviceIdentifier(generator, FIRMWARE_MANIFEST_TARGET_DEVICE_ID, builder.getTargetDeviceIdentifier());

      List<FirmwarePayloadBuilder> payloads = builder.getPayloads();
      if (!payloads.isEmpty()) {
        generator.writeFieldId(FIRMWARE_MANIFEST_PAYLOAD_ENTRY);

        if (payloads.size() == 1) {
          generate(generator, payloads.iterator().next());
        } else {
          generator.writeStartArray();
          for (FirmwarePayloadBuilder payload : payloads) {
            generate(generator, payload);
          }
          generator.writeEndArray();
        }
      }

      Map<Integer, byte[]> extensions = builder.getExtensions();
      if (!extensions.isEmpty()) {
        generator.writeFieldId(FIRMWARE_MANIFEST_SIMPLE_EXTENSIONS);
        generator.writeStartObject();

        for (Map.Entry<Integer, byte[]> entry : extensions.entrySet()) {
          writeBinaryField(generator, entry.getKey(), entry.getValue());
        }
        generator.writeEndObject();
      }

      generator.writeEndObject();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void generate(JsonGenerator generator, FirmwarePayloadBuilder builder) throws IOException {
    generator.writeStartObject();

    writeField(generator, FIRMWARE_PAYLOAD_ID, builder.getId());

    if (builder.getPackageIdentifier() != null) {
      writeTextField(generator, FIRMWARE_PAYLOAD_PACKAGE_ID, builder.getPackageIdentifier());
    }

    if (builder.getDescription() != null) {
      writeTextField(generator, FIRMWARE_MANIFEST_DESCRIPTION, builder.getDescription());
    }

    {
      generator.writeFieldId(FIRMWARE_PAYLOAD_FORMAT);
      generator.writeStartObject();
      writeIntegerField(generator, FIRMWARE_PAYLOAD_FORMAT_TYPE, builder.getFormatType());

      if (builder.getFormatGuidance() != null) {
        writeBinaryField(generator, FIRMWARE_PAYLOAD_FORMAT_GUIDANCE, builder.getFormatGuidance());
      }
      generator.writeEndObject();
    }

    writeIntegerField(generator, FIRMWARE_PAYLOAD_SIZE, builder.getSize());

    // private static final long FIRMWARE_PAYLOAD_SIMPLE_VERSION = 107L; // uint
    // private static final long FIRMWARE_PAYLOAD_VERSION = 108L; // object

    List<FirmwarePayloadDigest> digests = builder.getDigests();
    if (!digests.isEmpty()) {
      generator.writeFieldId(FIRMWARE_PAYLOAD_DIGESTS);

      generator.writeStartArray();
      for (FirmwarePayloadDigest digest : digests) {
        generate(generator, digest);
      }
      generator.writeEndArray();
    }

    // private static final long FIRMWARE_PAYLOAD_TARGET_COMPONENT_INDEX = 79L; //
    // text

    writeField(generator, FIRMWARE_PAYLOAD_STORAGE_IDENTIFIER, builder.getStorageId());

    // private static final long FIRMWARE_PAYLOAD_CONDITIONS = 101L; // array of
    // objects
    // private static final long FIRMWARE_PAYLOAD_DIRECTIVES = 104L; // array of
    // objects
    // private static final long FIRMWARE_PAYLOAD_TARGET_DEPENDENCY = 81L; // array
    // of objects
    // private static final long FIRMWARE_PAYLOAD_TARGET_MINIMAL_VERSION = 92L; //
    // object
    // private static final long FIRMWARE_PAYLOAD_RELATIONSHIPS = 84L; // enum (int)

    if (builder.getFirmwarePackage() != null) {
      writePayload(generator, FIRMWARE_PAYLOAD_PACKAGE, builder.getFirmwarePackage());
    }
    // private static final long FIRMWARE_PAYLOAD_SIMPLE_EXTENSIONS = 116L; // { +
    // int => bytes

    generator.writeEndObject();
  }

  private void generate(JsonGenerator generator, FirmwarePayloadDigest digest) throws IOException {
    generator.writeStartObject();

    writeIntegerField(generator, FIRMWARE_PAYLOAD_DIGEST_TYPE, digest.getType().getIndex());

    if (digest.getGuidance() != null) {
      writeBinaryField(generator, FIRMWARE_PAYLOAD_DIGEST_GUIDANCE, digest.getGuidance());
    }

    writeBinaryField(generator, FIRMWARE_PAYLOAD_DIGEST_VALUE, digest.getValue());
    generator.writeEndObject();
  }

  private void writeDeviceIdentifier(JsonGenerator generator, long fieldId, DeviceIdentifier deviceIdentifier)
      throws IOException {
    generator.writeFieldId(fieldId);

    generator.writeStartObject();

    writeTextField(generator, FIRMWARE_TARGET_DEVICE_IDENTIFIER_VENDOR, deviceIdentifier.getVendor());

    if (deviceIdentifier.getType() != null) {
      writeTextField(generator, FIRMWARE_TARGET_DEVICE_IDENTIFIER_TYPE, deviceIdentifier.getType());
    }

    writeTextField(generator, FIRMWARE_TARGET_DEVICE_IDENTIFIER_MODEL, deviceIdentifier.getVendor());

    if (deviceIdentifier.getClazz() != null) {
      writeTextField(generator, FIRMWARE_TARGET_DEVICE_IDENTIFIER_CLASS, deviceIdentifier.getClazz());
    }

    if (deviceIdentifier.getRfc4122() != null) {
      writeTextField(generator, FIRMWARE_TARGET_DEVICE_IDENTIFIER_RFC4122, deviceIdentifier.getRfc4122());
    }

    if (deviceIdentifier.getIeee8021ar() != null) {
      writeBinaryField(generator, FIRMWARE_TARGET_DEVICE_IDENTIFIER_IEEE802_1_AR, deviceIdentifier.getIeee8021ar());
    }

    generator.writeEndObject();
  }

  private void writePayload(JsonGenerator generator, long fieldId, FirmwarePayloadPackage payload) throws IOException {
    generator.writeFieldId(fieldId);

    generator.writeStartObject();

    if (payload.getCompressionType() != null) {
      writeIntegerOrTextField(generator, FIRMWARE_PAYLOAD_PACKAGE_COMPRESSION_TYPE, payload.getCompressionType());
    }

    if (payload.getCompressionGuidance() != null) {
      writeBinaryField(generator, FIRMWARE_PAYLOAD_PACKAGE_COMPRESSION_GUIDANCE, payload.getCompressionGuidance());
    }
    writeBinaryField(generator, FIRMWARE_PAYLOAD_PACKAGE_VALUE, payload.getPackageBytes());

    generator.writeEndObject();
  }
}
