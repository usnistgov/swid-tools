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

package gov.nist.secauto.swid.builder;

import gov.nist.secauto.swid.builder.util.Util;

import java.net.URI;
import java.util.Objects;

public class LinkBuilder
    extends AbstractLanguageSpecificBuilder<LinkBuilder> {

  public static LinkBuilder create() {
    return new LinkBuilder();
  }

  private String artifact;
  private URI href;
  private String media;
  private LinkOwnership ownership;
  private String rel;
  private String mediaType;
  private LinkUse use;

  protected LinkBuilder() {
    super();
  }

  @Override
  public void reset() {
    this.artifact = null;
    this.href = null;
    this.media = null;
    this.ownership = null;
    this.rel = null;
    this.mediaType = null;
    this.use = null;
  }

  public String getArtifact() {
    return artifact;
  }

  public URI getHref() {
    return href;
  }

  public String getMedia() {
    return media;
  }

  public LinkOwnership getOwnership() {
    return ownership;
  }

  public String getRel() {
    return rel;
  }

  public String getMediaType() {
    return mediaType;
  }

  public LinkUse getUse() {
    return use;
  }

  /**
   * For use with rel="installationmedia" to identify the canonical name for an installation media
   * resource.
   * 
   * @param artifact
   *          a cononical name for the installation media
   * @return the same builder instance
   */
  public LinkBuilder artifact(String artifact) {
    Util.requireNonEmpty(artifact, "artifact");
    this.artifact = artifact;
    return this;
  }

  /**
   * Sets the to-be-built link's href to the provided value.
   * 
   * @param uri
   *          a URI identifying the linked resource
   * @return the same builder instance
   */
  public LinkBuilder href(URI uri) {
    Objects.requireNonNull(uri, "url");
    this.href = uri;
    return this;
  }

  /**
   * Sets the to-be-built link's media to the provided value.
   * 
   * @param query
   *          a media query as defined by ISO/IEC 19770-2:2015
   * @return the same builder instance
   */
  public LinkBuilder media(String query) {
    Util.requireNonEmpty(query, "query");
    this.media = query;
    return this;
  }

  /**
   * Sets the to-be-built link's ownership to the provided value.
   * 
   * @param ownership
   *          a valid non-null enumeration value
   * @return the same builder instance
   */
  public LinkBuilder ownership(LinkOwnership ownership) {
    Objects.requireNonNull(ownership, "ownership");
    this.ownership = ownership;
    return this;
  }

  /**
   * Sets the to-be-built link's rel to the provided value.
   * 
   * @param rel
   *          the link relation type
   * @return the same builder instance
   */
  public LinkBuilder rel(String rel) {
    Util.requireNonEmpty(rel, "rel");
    this.rel = rel;
    return this;
  }

  /**
   * Provide the IANA MediaType for the resource targeted by the href attribute; this provides the
   * consumer with knowledge of the format of the referenced resource.
   * 
   * The <a href="http://www.iana.org/assignments/media-types/media-types.xhtml">The IANA Media Type
   * Registry</a> provides more details on possible values.
   * 
   * @param mediaType
   *          a valid media type value
   * @return the same builder instance
   */
  public LinkBuilder mediaType(String mediaType) {
    Util.requireNonEmpty(mediaType, "mediaType");
    this.mediaType = mediaType;
    return this;
  }

  /**
   * Sets the to-be-built link's use to the provided value.
   * 
   * @param use
   *          a valid, non-null enumeration value
   * @return the same builder instance
   */
  public LinkBuilder use(LinkUse use) {
    Objects.requireNonNull(use, "use");
    this.use = use;
    return this;
  }

  @Override
  public void validate() throws ValidationException {
    super.validate();
    validateNonNull("href", href);
    validateNonEmpty("href", href.toString());
    validateNonEmpty("href", rel);
  }
}
