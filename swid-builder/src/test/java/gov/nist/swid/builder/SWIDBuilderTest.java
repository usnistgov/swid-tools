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

package gov.nist.swid.builder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import gov.nist.swid.builder.output.CBOROutputHandler;
import gov.nist.swid.builder.output.JsonOutputHandler;
import gov.nist.swid.builder.output.XMLOutputHandler;
import gov.nist.swid.builder.resource.HashAlgorithm;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

public class SWIDBuilderTest {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private static SWIDBuilder buildSWID() {
    SWIDBuilder builder = SWIDBuilder.create();
//    builder.name("Test Product").version("1.0.0").tagId(UUID.randomUUID().toString())
//        .addEntity(EntityBuilder.create().regid("gov.acme")
//            .name("ACME Software")
//            .addRole(KnownRole.SOFTWARE_CREATOR))
//        .addEntity(EntityBuilder.create().regid("gov.nist")
//            .name("National Institute of Standards and Technology, United States Department of Commerce")
//            .addRole(KnownRole.TAG_CREATOR));
////            .addRole(KnownRole.TAG_CREATOR).addRole(KnownRole.SOFTWARE_CREATOR));
    builder.name("ACME Roadrunner Detector 2013 Coyote Edition SP1").version("4.1.5").tagId("com.acme.rrd2013-ce-sp1-v4-1-5-0")
    .addEntity(EntityBuilder.create().regid("acme.com")
        .name("The ACME Corporation")
        .addRole(KnownRole.SOFTWARE_CREATOR).addRole(KnownRole.TAG_CREATOR))
    .addLink(LinkBuilder.create().rel("license").href(URI.create("http://www.gnu.org/licenses/gpl.txt")))
    .addMeta(MetaBuilder.create().setProductBaseName("Roadrunner Detector").setColloquialVersion("2013").setEdition("coyote").setRevision("sp1"))
    .newPayload().newFileResource(Collections.singletonList("rrdetector.exe")).size(532712).hash(HashAlgorithm.SHA_256, "a314fc2dc663ae7a6b6bc6787594057396e6b3f569cd50fd5ddb4d1bbafd2b6a");

    return builder;
  }

  @Test
  public void testCBOR() throws IOException, NoSuchAlgorithmException, ValidationException {
    SWIDBuilder builder = buildSWID();
    File file = folder.newFile();
    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
    new CBOROutputHandler().write(builder, os);
    os.close();
    System.out.println("CBOR: "+file.length());
  }

  @Test
  public void testJson() throws IOException, NoSuchAlgorithmException, ValidationException {
    SWIDBuilder builder = buildSWID();
    File file = folder.newFile();
    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
    JsonFactory jsonFactory = new JsonFactory();
    new JsonOutputHandler(jsonFactory) {

      /* (non-Javadoc)
       * @see gov.nist.swid.builder.output.AbstractJsonOutputHandler#newGenerator(java.io.OutputStream)
       */
      @Override
      protected JsonGenerator newGenerator(OutputStream os) throws IOException {
        JsonGenerator retval = super.newGenerator(os);
        retval.useDefaultPrettyPrinter();
        return retval;
      }
      
    }.write(builder, os);
    os.close();
  }

  @Test
  public void testXML() throws IOException, ValidationException {
    SWIDBuilder builder = buildSWID();

    File file = folder.newFile();

    try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
      new XMLOutputHandler().write(builder, os);
    }
    System.out.println("XML: "+file.length());
  }

}
