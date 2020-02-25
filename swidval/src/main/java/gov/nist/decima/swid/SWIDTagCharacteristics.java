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
package gov.nist.decima.swid;

import gov.nist.decima.xml.document.XMLDocument;
import gov.nist.decima.xml.document.XPathEvaluator;
import gov.nist.decima.xml.document.XPathNamespaceContext;

import java.util.Objects;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

public class SWIDTagCharacteristics {
  /**
   * Detects the SWID tag characteristics for the provided file.
   * 
   * @param doc
   *          the file to generate characteristics for
   * @return the detected SWID tag characteristics
   * @throws XPathExpressionException
   *           if an error occurred while evaluating the XPath expressions used to determine the
   *           characteristics
   * @throws XPathFactoryConfigurationException
   *           if an error occurred while establishing the XPath evaluation environment
   */
  public static SWIDTagCharacteristics getSWIDTagCharacteristics(XMLDocument doc)
      throws XPathExpressionException, XPathFactoryConfigurationException {
    XPathEvaluator eval = doc.newXPathEvaluator();
    XPathNamespaceContext nsContext = new XPathNamespaceContext();
    nsContext.addNamespace("swid", "http://standards.iso.org/iso/19770/-2/2015/schema.xsd");
    eval.setNamespaceContext(nsContext);

    boolean authoritative = eval.test("//swid:Entity[contains(@role,'tagCreator') and "
        + "(contains(@role,'aggregator') or contains(@role,'distributor') or contains(@role,'licensor') or "
        + "contains(@role,'softwareCreator'))]");
    String tagTypeValue = eval.evaluateSingle(
        "if (/swid:SoftwareIdentity[@patch='true']) then 'patch' "
            + "else (if (/swid:SoftwareIdentity[@supplemental='true']) then 'supplemental' "
            + "else (if (/swid:SoftwareIdentity[@corpus='true']) then 'corpus' else 'primary'))",
        XPathConstants.STRING, null);
    TagType tagType = TagType.lookup(tagTypeValue);
    return new SWIDTagCharacteristics(tagType, authoritative);
  }

  private final TagType tagType;
  private final boolean authoritative;

  /**
   * Construct a new SWID tag characteristics using the provided characteristics.
   * 
   * @param tagType
   *          the type of the tag
   * @param authoritative
   *          <code>true</code> if the tag is authoritative, or <code>false</code> otherwise
   */
  public SWIDTagCharacteristics(TagType tagType, boolean authoritative) {
    Objects.requireNonNull(tagType, "tagType");
    this.tagType = tagType;
    this.authoritative = authoritative;
  }

  /**
   * Retrieve the tag type.
   * 
   * @return the tagType
   */
  public TagType getTagType() {
    return tagType;
  }

  /**
   * Determine if the tag is authoritative or not.
   * 
   * @return <code>true</code> if the tag is authoritative, or <code>false</code> otherwise
   */
  public boolean isAuthoritative() {
    return authoritative;
  }
}
