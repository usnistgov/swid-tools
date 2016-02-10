package gov.nist.decima.swid;

public enum TagType {
	CORPUS("corpus"),
	PRIMARY("primary"),
	PATCH("patch"),
	SUPPLEMENTAL("supplemental");

	private final String name;

	private TagType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
