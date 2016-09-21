package gov.nist.decima.swid;

import java.util.HashMap;
import java.util.Map;

public enum TagType {
	CORPUS("corpus"),
	PRIMARY("primary"),
	PATCH("patch"),
	SUPPLEMENTAL("supplemental");

	private static final Map<String, TagType> lookupMap = new HashMap<>();

	static {
		for (TagType type : TagType.values()) {
			lookupMap.put(type.getName(), type);
		}
	}

	public static TagType lookup(String value) {
		return lookupMap.get(value);
	}

	private final String name;

	private TagType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
