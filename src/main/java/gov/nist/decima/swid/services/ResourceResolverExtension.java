package gov.nist.decima.swid.services;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ext.EntityResolver2;

// TODO: Remove this?
public class ResourceResolverExtension implements gov.nist.decima.core.service.ResourceResolverExtension {
//	private static final ExtendedEntityResolver RESOLVER = new ExtendedEntityResolver();

	@Override
	public EntityResolver2 getEntityResolver() {
		return null;
//		return RESOLVER;
	}

	@Override
	public LSResourceResolver getLSResourceResolver() {
		return null;
//		return RESOLVER;
	}

//	private static class ExtendedEntityResolver implements EntityResolver2, LSResourceResolver {
//		private static final Map<String, URL> resourceMap = new HashMap<>();
//		static {
//			try {
//				resourceMap.put("http://www.w3.org/TR/xmldsig-core/xmldsig-core-schema.xsd", new URL("classpath:schema/xmldsig-core-schema.xsd"));
//				resourceMap.put("http://www.w3.org/2001/XMLSchema.dtd", new URL("classpath:schema/XMLSchema.dtd"));
//				resourceMap.put("http://www.w3.org/2001/xml.xsd", new URL("classpath:schema/xml.xsd"));
//				resourceMap.put("http://www.w3.org/2001/datatypes.dtd", new URL("classpath:schema/datatypes.dtd"));
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//		}
//
//		@Override
//		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
//			return null;
//		}
//
//		@Override
//		public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
//			return null;
//		}
//
//		@Override
//		public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId)
//				throws SAXException, IOException {
//			URI systemURI = resolve(baseURI, systemId);
//
//			InputSource retval = null;
//			String key = systemURI.toString();
//			URL result = resourceMap.get(key);
//			if (result != null) {
//				retval = new InputSource(result.openStream());
//				retval.setSystemId(key);
//			}
//			return retval;
//		}
//
//		private URI resolve(String baseURI, String systemId) {
//			URI retval;
//			if (baseURI != null) {
//				URI base = URI.create(baseURI);
//				retval = base.resolve(systemId);
//			} else {
//				retval = URI.create(systemId);
//			}
//			return retval;
//		}
//
//		@Override
//		public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
//				String baseURI) {
//			return null;
//		}
//		
//	}
}
