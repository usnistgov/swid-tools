# Software Identification (SWID) Tag Tools

This project provides a set of Java-based tools for the generation and validation of Software Identification (SWID) tags produced by the NIST [SWID Tagging Project](https://csrc.nist.gov/projects/Software-Identification-SWID). These tools support XML-based SWID tags based on the format defined by ISO/IEC 19770-2:2015, and [Concise Binary Object Representation](https://cbor.io/) (CBOR) based concise SWID (CoSWID) tags based on the [IETF CoSWID](https://datatracker.ietf.org/doc/draft-ietf-sacm-coswid/) specification.

Included in this repository are the sources for:

- [swid-builder](swid-builder/): A Java [API](swid-builder/apidocs/index.html) for building SWID and CoSWID tags.
- [swidval](swidval/): A command line tool and [API](swidval/apidocs/index.html) for validating SWID and CoSWID tags against schema requirements and best practice guidance.
- [swid-maven-plugin](swid-maven-plugin/): Supports SWID generation in an [Apache Maven](http://maven.apache.org) build environment.
- [swidval-webapp](swidval-webapp/): A simple, proof of concept webapp that provides a SWID validation service that is deployable to a Java application server.
- [swid-repo-client](swid-repo-client/): A Java-based client for posting SWID tags to the [National Vulnerability Database](https://nvd.nist.gov/) (NVD).

Please refer to each sub-module for usage instructions.
