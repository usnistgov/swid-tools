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

import gov.nist.swid.builder.AbstractBuilder;
import gov.nist.swid.builder.AbstractFileSystemItemBuilder;
import gov.nist.swid.builder.AbstractResourceBuilder;
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
import gov.nist.swid.builder.resource.HashAlgorithm;
import gov.nist.swid.builder.resource.PathRelativizer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class XMLOutputHandler implements OutputHandler {
  public static final Namespace SWID_NAMESPACE
      = Namespace.getNamespace("http://standards.iso.org/iso/19770/-2/2015/schema.xsd");

  private final Format format;

  public XMLOutputHandler() {
    this(Format.getPrettyFormat());
  }

  public XMLOutputHandler(Format format) {
    this.format = format;
  }

  @Override
  public void write(SWIDBuilder builder, OutputStream os) throws IOException {
    XMLOutputter out = new XMLOutputter(format);
    out.output(generateXML(builder), os);
  }

  /**
   * Creates a JDOM2 XML Document based on the content of the builder.
   * @param builder the {@link SWIDBuilder} to use the information from to build the XML model
   * @return a JDOM2 {@link Document} based on the SWID information
   */
  public Document generateXML(SWIDBuilder builder) {
    builder.validate();

    Document retval = buildDocument(builder);
    return retval;
  }

  protected Document buildDocument(SWIDBuilder builder) {
    return new Document(build(builder));
  }

  protected Element build(SWIDBuilder builder) {
    Element element = new Element("SoftwareIdentity", SWID_NAMESPACE);

    buildAbstractBuilder(builder, element);

    // required attributes
    element.setAttribute("name", builder.getName());
    element.setAttribute("tagId", builder.getTagId());

    // optional attributes
    switch (builder.getTagType()) {
    case PRIMARY:
      break;
    case CORPUS:
      element.setAttribute("corpus", Boolean.TRUE.toString());
      break;
    case PATCH:
      element.setAttribute("patch", Boolean.TRUE.toString());
      break;
    case SUPPLEMENTAL:
      element.setAttribute("supplemental", Boolean.TRUE.toString());
      break;
    default:
      throw new IllegalStateException("tagType: " + builder.getTagType().toString());
    }

    element.setAttribute("tagVersion", Integer.toString(builder.getTagVersion()));

    buildAttribute("version", builder.getVersion(), element);
    buildAttribute("versionScheme", builder.getVersionScheme(), element);

    // child elements
    // Required
    for (EntityBuilder entity : builder.getEntities()) {
      element.addContent(build(entity));
    }

    // optional
    EvidenceBuilder evidence = builder.getEvidence();
    if (evidence != null) {
      element.addContent(build(evidence));
    }

    for (LinkBuilder link : builder.getLinks()) {
      element.addContent(build(link));
    }

    for (MetaBuilder meta : builder.getMetas()) {
      element.addContent(build(meta));
    }

    PayloadBuilder payload = builder.getPayload();
    if (payload != null) {
      element.addContent(build(payload));
    }

    return element;
  }

  protected Element build(EntityBuilder builder) {
    Element element = new Element("Entity", SWID_NAMESPACE);

    buildAbstractBuilder(builder, element);

    // required attributes
    element.setAttribute("name", builder.getName());

    StringBuilder sb = null;
    for (String role : builder.getRoles()) {
      if (sb == null) {
        sb = new StringBuilder();
      } else {
        sb.append(' ');
      }
      sb.append(role);
    }
    element.setAttribute("role", sb.toString());

    // optional attributes
    buildAttribute("regid", builder.getRegid(), element);
    buildAttribute("thumbprint", builder.getThumbprint(), element);

    return element;
  }

  protected Element build(EvidenceBuilder builder) {
    Element element = new Element("Evidence", SWID_NAMESPACE);

    buildAbstractResourceCollectionBuilder(builder, element);

    ZonedDateTime date = builder.getDate();
    if (date != null) {
      element.setAttribute("date", date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
    buildAttribute("deviceId", builder.getDeviceId(), element);

    return element;
  }

  protected Element build(LinkBuilder builder) {
    Element element = new Element("Link", SWID_NAMESPACE);

    buildAbstractBuilder(builder, element);

    // required attributes
    element.setAttribute("href", builder.getHref().toString());
    element.setAttribute("rel", builder.getRel());

    // optional attributes
    buildAttribute("artifact", builder.getArtifact(), element);
    buildAttribute("media", builder.getMedia(), element);
    buildAttribute("ownership", builder.getOwnership(), element);
    buildAttribute("type", builder.getMediaType(), element);
    buildAttribute("use", builder.getUse(), element);

    return element;
  }


  protected Element build(MetaBuilder builder) {
    Element element = new Element("Meta", SWID_NAMESPACE);

    buildAttribute("activationStatus", builder.getActivationStatus(), element);
    buildAttribute("channelType", builder.getChannelType(), element);
    buildAttribute("colloquialVersion", builder.getColloquialVersion(), element);
    buildAttribute("description", builder.getDescription(), element);
    buildAttribute("edition", builder.getEdition(), element);
    buildAttribute("entitlementDataRequired", builder.getEntitlementDataRequired(), element);
    buildAttribute("entitlementKey", builder.getEntitlementKey(), element);
    buildAttribute("generator", builder.getGenerator(), element);
    buildAttribute("persistentId", builder.getPersistentId(), element);
    buildAttribute("product", builder.getProductBaseName(), element);
    buildAttribute("productFamily", builder.getProductFamily(), element);
    buildAttribute("revision", builder.getRevision(), element);
    buildAttribute("summary", builder.getSummary(), element);
    buildAttribute("unspscCode", builder.getUnspscCode(), element);
    buildAttribute("unspscVersion", builder.getUnspscVersion(), element);
    
    return element;
  }

  protected Element build(PayloadBuilder builder) {
    Element element = new Element("Payload", SWID_NAMESPACE);

    buildAbstractResourceCollectionBuilder(builder, element);

    return element;
  }

  private static <E extends AbstractResourceCollectionBuilder<E>> void buildAbstractResourceCollectionBuilder(
      AbstractResourceCollectionBuilder<E> builder, Element element) {
    buildAbstractBuilder(builder, element);

    ResourceCollectionEntryGenerator creator = new XMLResourceCollectionEntryGenerator(element);
    for (ResourceBuilder resourceBuilder : builder.getResources()) {
      resourceBuilder.accept(creator);
    }
  }

  private static <E extends AbstractBuilder<E>> void buildAbstractBuilder(AbstractBuilder<E> builder, Element element) {
    String language = builder.getLanguage();
    if (language != null) {
      element.setAttribute("lang", language, Namespace.XML_NAMESPACE);
    }
  }

  private static void buildAttribute(String attributeName, String value, Element element) {
    if (value != null) {
      element.setAttribute(attributeName, value);
    }
  }

  private static void buildAttribute(String attributeName, Object value, Element element) {
    if (value != null) {
      element.setAttribute(attributeName, value.toString());
    }
  }

  private static class XMLResourceCollectionEntryGenerator implements ResourceCollectionEntryGenerator {
    private final Element element;

    public XMLResourceCollectionEntryGenerator(Element element) {
      this.element = element;
    }

    @Override
    public void generate(FileBuilder builder) {

      Element element = new Element("File", XMLOutputHandler.SWID_NAMESPACE);
      this.element.addContent(element);

      buildAbstractFileSystemItem(builder, element);

      XMLOutputHandler.buildAttribute("size", builder.getSize(), element);
      XMLOutputHandler.buildAttribute("version", builder.getVersion(), element);

      Element parent = element.getParentElement();
      for (Map.Entry<HashAlgorithm, String> entry : builder.getHashAlgorithmToValueMap().entrySet()) {
        HashAlgorithm algorithm = entry.getKey();
        String hashValue = entry.getValue();
        Namespace ns = Namespace.getNamespace(algorithm.getName(), algorithm.getNamespace());
        Namespace nsOld = parent.getNamespace(ns.getPrefix());

        if (nsOld == null) {
          parent.addNamespaceDeclaration(ns);
        } else if (!nsOld.getURI().equals(ns.getURI())) {
          element.addNamespaceDeclaration(ns);
        }
        element.setAttribute("hash", hashValue, ns);
      }
    }

    private static <E extends AbstractFileSystemItemBuilder<E>> void buildAbstractFileSystemItem(
        AbstractFileSystemItemBuilder<E> builder, Element element) {
      buildAbstractResourceBuilder(builder, element);

      XMLOutputHandler.buildAttribute("root", builder.getRoot(), element);
      List<String> location = builder.getLocation();
      if (location != null && !location.isEmpty()) {
        element.setAttribute("location", PathRelativizer.toURI(location).toString());
      }
      XMLOutputHandler.buildAttribute("name", builder.getName(), element);
      XMLOutputHandler.buildAttribute("key", builder.getKey(), element);
    }

    private static <E extends AbstractResourceBuilder<E>> void buildAbstractResourceBuilder(
        AbstractResourceBuilder<E> builder, Element element) {
      XMLOutputHandler.buildAbstractBuilder(builder, element);
    }
  }
}
