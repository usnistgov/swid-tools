package gov.nist.decima.swid.services;

import net.sf.saxon.Configuration;

public class TransformerExtension implements gov.nist.decima.service.TransformerExtension {

	@Override
	public void registerExtensions(Configuration config) {
		config.registerExtensionFunction(new IsStringSetEqual());
	}

}
