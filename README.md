# swid-builder
Provides Maven build support for all Security Automation related projects.

This project provides the following features:
- Centralized control for management of Maven plugin versions
- Code style checking using the [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/). The Checkstyle rules used are defined in the build-support submodule.
- Source code analysis using the [Maven PMD Plugin](https://maven.apache.org/plugins/maven-pmd-plugin/) to look for highly likely and critically broken/buggy code (priority: 2).
- License checking to ensure that the NIST license is applied to all source files. The license used is configured in the build-support submodule.

These features can be added to a project by making the oss-paren submodule the parent of any other maven project. For example:

    <project xmlns="http://maven.apache.org/POM/4.0.0">
        <modelVersion>4.0.0</modelVersion>
        <parent>
            <groupId>gov.nist.secauto</groupId>
            <artifactId>oss-parent</artifactId>
            <version>${project.version}</version>
        </parent>
        ...
    </project>
