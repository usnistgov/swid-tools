# Software Identification (SWID) Tag Maven Plugin

This submodule supports generating a SWID tag as part of the [Apache Maven](https://maven.apache.org/) build system.

The submodule provides the following capabilities:
- A [container descriptor handler](http://maven.apache.org/plugins/maven-assembly-plugin/examples/single/using-container-descriptor-handlers.html) that supports generation of a SWID tag as a manifest of a Maven [Assembly](http://maven.apache.org/plugins/maven-assembly-plugin/). Maven assemblies are binary distributions of Java code archived using a number of popular archive formats.
- An experimental Maven mojo for building a SWID tag for use within a Java JAR file.

The source for these tools are found in this repo and can be built using Apache Maven as follows:

```bash
mvn clean install
```

Instructions for [using these tools](https://pages.nist.gov/swid-tools/swid-maven-plugin/) can be found on the project's website.
