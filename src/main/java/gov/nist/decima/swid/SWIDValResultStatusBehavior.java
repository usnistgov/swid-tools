package gov.nist.decima.swid;

import java.util.Map;
import java.util.Set;

import gov.nist.decima.core.assessment.result.ResultStatusBehavior;
import gov.nist.decima.core.requirement.Requirement;

public class SWIDValResultStatusBehavior implements ResultStatusBehavior {
	private final TagType tagType;
	private final boolean authoritative;

	public SWIDValResultStatusBehavior(TagType tagType, boolean authoritative) {
		this.tagType = tagType;
		this.authoritative = authoritative;
	}

	@Override
	public boolean isInScope(Requirement requirement) {
		Map<String, Set<String>> tags = requirement.getMetadataTagValueMap();

		Set<String> tagTypes = tags.get("{http://decima.nist.gov/xml/swid-requirements-ext/0.1}tag-type");
		Set<String> scopeValues = tags.get("{http://decima.nist.gov/xml/swid-requirements-ext/0.1}scope");
		String scope = authoritative ? "authoritative" : "non-authoritative";
		boolean matchTagType = tagTypes == null || tagTypes.isEmpty() || tagTypes.contains("all") || tagTypes.contains(tagType.getName());
		boolean matchScope = scopeValues == null || scopeValues.isEmpty() || scopeValues.contains("all") || scopeValues.contains(scope);
		return matchTagType && matchScope;
	}

	
}
