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

package gov.nist.swid.builder.resource;

public enum HashAlgorithm {
  SHA_256(1, "SHA-256", 256, "http://www.w3.org/2001/04/xmlenc#sha256"),
  SHA_256_128(2, "SHA_256_128", 128, "http://www.w3.org/2001/04/xmlenc#sha256"),
  SHA_256_120(3, "SHA_256_120", 120, "http://www.w3.org/2001/04/xmlenc#sha256"),
  SHA_256_96(4, "SHA_256_96", 96, "http://www.w3.org/2001/04/xmlenc#sha256"),
  SHA_256_64(5, "SHA_256_64", 64, "http://www.w3.org/2001/04/xmlenc#sha256"),
  SHA_256_32(6, "SHA_256_32", 32, "http://www.w3.org/2001/04/xmlenc#sha256"),
  SHA_384(7, "SHA_384", 384, "http://www.w3.org/2001/04/xmldsig-more#sha384"),
  SHA_512(8, "SHA-512", 512, "http://www.w3.org/2001/04/xmlenc#sha512"),
  SHA_3_224(9, "SHA_3_224", 224, "http://www.w3.org/2007/05/xmldsig-more#sha3-224"),
  SHA_3_256(9, "SHA_3_256", 256, "http://www.w3.org/2007/05/xmldsig-more#sha3-256"),
  SHA_3_384(9, "SHA_3_384", 384, "http://www.w3.org/2007/05/xmldsig-more#sha3-384"),
  SHA_3_512(9, "SHA_3_512", 512, "http://www.w3.org/2007/05/xmldsig-more#sha3-512");

  /**
   * From the Named Information Hash Algorithm Registry:
   * https://www.iana.org/assignments/named-information/named-information.xhtml#hash-alg
   */
  private final int index;
  private final String name;
  /**
   * The value length (in bits).
   */
  private final int valueLength;
  private final String namespace;

  private HashAlgorithm(int index, String name, int valueLength, String namespace) {
    this.index = index;
    this.name = name;
    this.valueLength = valueLength;
    this.namespace = namespace;
  }

  /**
   * Retrieve the integer idenx value.
   * 
   * @return the index value
   */
  public int getIndex() {
    return index;
  }

  /**
   * Retrieve the human-readable text value.
   * 
   * @return the text name of the algorithm
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieve the length in bits for the digest value. This can be used to truncate the digest result
   * if the algorithm requires this.
   * 
   * @return the valueLength (in bits)
   */
  public int getValueLength() {
    return valueLength;
  }

  /**
   * The namespace URI for the hash algorithm.
   * 
   * @return a URI
   */
  public String getNamespace() {
    return namespace;
  }
}
