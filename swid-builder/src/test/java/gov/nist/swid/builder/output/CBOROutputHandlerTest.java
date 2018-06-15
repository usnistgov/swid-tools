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

import gov.nist.swid.builder.EntityBuilder;
import gov.nist.swid.builder.KnownRole;
import gov.nist.swid.builder.KnownVersionScheme;
import gov.nist.swid.builder.SWIDBuilder;
import gov.nist.swid.builder.TagType;
import gov.nist.swid.builder.ValidationException;
import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.HashUtils;
import gov.nist.swid.builder.resource.firmware.DeviceIdentifier;
import gov.nist.swid.builder.resource.firmware.DigestType;
import gov.nist.swid.builder.resource.firmware.FirmwareBuilder;
import gov.nist.swid.builder.resource.firmware.FirmwarePayloadBuilder;
import gov.nist.swid.builder.resource.firmware.FirmwarePayloadPackage;
import gov.nist.swid.builder.resource.firmware.StringFirmwareIdentifier;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

public class CBOROutputHandlerTest {

    @Test
    public void testCBOR() throws IOException, ValidationException {
        SWIDBuilder builder = SWIDBuilder.create();
        builder.addEntity(
                EntityBuilder.create().name("NIST").regid("nist.gov").addRole(KnownRole.TAG_CREATOR).addRole(KnownRole.SOFTWARE_CREATOR));
        builder.language("en-US").name("coswid app").tagId("tagId").tagType(TagType.PRIMARY).version("1.0.0")
                .versionScheme(KnownVersionScheme.MULTIPART_NUMERIC);

        CBOROutputHandler cborHandler = new CBOROutputHandler();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File("swid-cbor.cbor")))) {
            cborHandler.write(builder, os);
        }

        XMLOutputHandler xmlHandler = new XMLOutputHandler();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File("swid-cbor.xml")))) {
            xmlHandler.write(builder, os);
        }
    }

    @Test
    public void testFirmware() throws IOException, ValidationException, NoSuchAlgorithmException {
        SWIDBuilder builder = SWIDBuilder.create();
        builder.addEntity(
                EntityBuilder.create().name("Acme Software").regid("acme.com").addRole(KnownRole.TAG_CREATOR).addRole(KnownRole.SOFTWARE_CREATOR));
        builder.language("en-US").name("acme firmware").tagId(UUID.randomUUID().toString()).tagType(TagType.PRIMARY).version("1.0.0")
                .versionScheme(KnownVersionScheme.MULTIPART_NUMERIC);

        FirmwareBuilder fb = builder.newPayload().newFirmwareResource();
        fb.id(new StringFirmwareIdentifier(UUID.randomUUID().toString()));
        fb.version(BigInteger.ONE);
        fb.targetDeviceIdentifier(new DeviceIdentifier("acme", "9000x"));
        
        FirmwarePayloadBuilder fp = new FirmwarePayloadBuilder();
        fp.id(new StringFirmwareIdentifier(UUID.randomUUID().toString()));
        fp.formatType(0);
        fp.size(BigInteger.valueOf(2048));

        byte[] payload = new byte[2048];
        new Random().nextBytes(payload);

        fp.addDigest(DigestType.RAW_PAYLOAD, HashAlgorithm.SHA_512, HashUtils.hash(HashAlgorithm.SHA_512, payload));
        fp.storageId(new StringFirmwareIdentifier("some storageId"));
        fp.firmwarePackage(new FirmwarePayloadPackage(payload));

        fb.addPayload(fp);

        CBOROutputHandler cborHandler = new CBOROutputHandler();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File("firmware-coswid.cbor")))) {
            cborHandler.write(builder, os);
        }
    }

}
