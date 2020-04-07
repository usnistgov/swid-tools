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

package gov.nist.secauto.swid.client.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PayloadBuilderServiceImpl implements PayloadBuilderService {

  // Element names, required for constructing REST request payload
  public static final String SWID_BODY_ELEMENT = "body";
  public static final String TAG_TYPE_ELEMENT = "tagType";
  public static final String SWID_ELEMENT = "swid";
  public static final String SWID_LIST_ELEMENT = "swidList";

  private static final String SOFTWARE_IDENTITY_ELEMENT = "<SoftwareIdentity";

  /**
   * Construct payload from one or more tag files
   */
  @Override
  public String buildPayload(List<String> tagFileNames, TagType tagType)
      throws UnsupportedEncodingException, IOException {
    String content = "";
    // Tagtype option provided will be used for all SWIDs posted
    String tagElement = constructTagTypeElement(tagType);
    for (String filename : tagFileNames) {
      content += constructSwidElementFromFile(Paths.get(filename), tagElement);
    }
    content = this.constructXMLElement(content, SWID_LIST_ELEMENT);
    return content;
  }

  /**
   * Wrap tagType, swidData into SWID element
   * 
   * @param path
   * @param tagElement
   * @return
   * @throws UnsupportedEncodingException
   * @throws IOException
   */
  private String constructSwidElementFromFile(Path path, String tagElement)
      throws UnsupportedEncodingException, IOException {
    String content = new String(Files.readAllBytes(path), "UTF-8");
    String swidData = "";
    // Remove the XML declaration provided in each SWID tag
    int swidDataStartIndex = content.indexOf(SOFTWARE_IDENTITY_ELEMENT);
    if (swidDataStartIndex > 0) {
      swidData = content.substring(swidDataStartIndex);
    }
    String bodyElement = constructBodyElement(swidData);
    return this.constructXMLElement(tagElement + bodyElement, SWID_ELEMENT);
  }

  /**
   * Wrap the swid data
   * 
   * @param swidData
   * @return
   */
  private String constructBodyElement(String swidData) {

    return this.constructXMLElement(swidData, SWID_BODY_ELEMENT);
  }

  /**
   * Wrap tag type
   * 
   * @param type
   * @return
   */
  private String constructTagTypeElement(TagType type) {
    if (type == null) {
      return "";
    }
    return this.constructXMLElement(type.getName(), TAG_TYPE_ELEMENT);
  }

  /**
   * Constructs XML element given data and element name
   * 
   * @param input
   * @param elementName
   * @return
   */
  private String constructXMLElement(String input, String elementName) {

    return "<" + elementName + ">" + input + "</" + elementName + ">";// TODO
    // check
    // this
  }
}
