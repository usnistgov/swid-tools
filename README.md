# Documentation for the SWID Tag Builder Project

## SWID Tag Tooling

The NIST Security Automation Program has developed the following resources to assist with creating SWID Tags.
* Java SWID Builder API - Supports generation of SWID tags in Java.
* Maven build support for the generation of SWID Tags.

These resources can be incorporated into existing packaging tools to produce SWID Tags.

## The Java SWID Builder API

SWID builder is a Java API that supports easy [SWID tag](https://scap.nist.gov/specifications/swid/) generation. The API used in the SWID builder is based on the [builder pattern](https://en.wikipedia.org/wiki/Builder_pattern) which provides for a factory based, polymorphic method of constructing a SWID Tag.

The following is an example of using the SWID Builder API to create a basic SWID Tag.

```java
SWIDBuilder builder = SWIDBuilder.create();
builder.name("Test Product").version("1.0.0").tagId(UUID.randomUUID().toString())
  .addEntity(EntityBuilder.create().regid("gov.nist")
	.name("National Institute of Standards and Technology, United States Department of Commerce")
	.addRole(SWIDConstants.ROLE_TAG_CREATOR).addRole(SWIDConstants.ROLE_SOFTWARE_CREATOR));

File file = new File("swid-tag.xml");
try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
	new XMLOutputHandler().write(builder, os);
}
```