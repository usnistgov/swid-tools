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

package gov.nist.decima.swid.services;

import org.apache.xerces.util.XMLCatalogResolver;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ext.EntityResolver2;

// TODO: Remove this?
public class ResourceResolverExtension
    implements gov.nist.decima.core.service.ResourceResolverExtension {
  // private static final ExtendedEntityResolver RESOLVER = new ExtendedEntityResolver();
  private static final XMLCatalogResolver RESOLVER = new XMLCatalogResolver(
      new String[] { "classpath:swidval-catalog.xml" });

  @Override
  public EntityResolver2 getEntityResolver() {
    return RESOLVER;
  }

  @Override
  public LSResourceResolver getLSResourceResolver() {
    return RESOLVER;
  }

  // private static class ExtendedEntityResolver implements EntityResolver2, LSResourceResolver {
  // private static final Map<String, URL> resourceMap = new HashMap<>();
  // static {
  // try {
  // resourceMap.put("http://www.w3.org/TR/xmldsig-core/xmldsig-core-schema.xsd", new
  // URL("classpath:schema/xmldsig-core-schema.xsd"));
  // resourceMap.put("http://www.w3.org/2001/XMLSchema.dtd", new
  // URL("classpath:schema/XMLSchema.dtd"));
  // resourceMap.put("http://www.w3.org/2001/xml.xsd", new URL("classpath:schema/xml.xsd"));
  // resourceMap.put("http://www.w3.org/2001/datatypes.dtd", new
  // URL("classpath:schema/datatypes.dtd"));
  // } catch (MalformedURLException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // @Override
  // public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
  // IOException {
  // return null;
  // }
  //
  // @Override
  // public InputSource getExternalSubset(String name, String baseURI) throws SAXException,
  // IOException {
  // return null;
  // }
  //
  // @Override
  // public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId)
  // throws SAXException, IOException {
  // URI systemURI = resolve(baseURI, systemId);
  //
  // InputSource retval = null;
  // String key = systemURI.toString();
  // URL result = resourceMap.get(key);
  // if (result != null) {
  // retval = new InputSource(result.openStream());
  // retval.setSystemId(key);
  // }
  // return retval;
  // }
  //
  // private URI resolve(String baseURI, String systemId) {
  // URI retval;
  // if (baseURI != null) {
  // URI base = URI.create(baseURI);
  // retval = base.resolve(systemId);
  // } else {
  // retval = URI.create(systemId);
  // }
  // return retval;
  // }
  //
  // @Override
  // public LSInput resolveResource(String type, String namespaceURI, String publicId, String
  // systemId,
  // String baseURI) {
  // return null;
  // }
  //
  // }
}
