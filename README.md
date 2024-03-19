# Software Identification (SWID) Tagging Tools and Utilities
[![Build Jobs](https://github.com/usnistgov/swid-tools/actions/workflows/build.yml/badge.svg)](https://github.com/usnistgov/swid-tools/actions/workflows/build.yml) [![Gitter](https://img.shields.io/gitter/room/swid-tools/community.svg?style=flat-square)](https://gitter.im/swid-tools/community)

This project provides a set of Java-based tools for the generation and validation of Software Identification (SWID) tags produced by the NIST [SWID Tagging Project](https://csrc.nist.gov/projects/Software-Identification-SWID). These tools support XML-based SWID tags based on the format defined by ISO/IEC 19770-2:2015, and [Concise Binary Object Representation](https://cbor.io/) (CBOR) based concise SWID (CoSWID) tags based on the [IETF CoSWID](https://datatracker.ietf.org/doc/draft-ietf-sacm-coswid/) specification.

Included in this repository are the sources for:

- [swid-builder](swid-builder): A Java API for building SWID and CoSWID tags, which can be used in other applications to produce tags in the XML and CBOR formats.
- [swidval](swidval): A command line tool for validating a SWID tag against the requirements defined by ISO/IEC 19770-2:2015, IETF CoSWID, and NIST Internal Report [(NISTIR) 8060](https://csrc.nist.gov/publications/detail/nistir/8060/final). This tool provides an convenient way to check if a SWID or CoSWID tag is valid and provides the necessary information required by the standard and best practices.
- [swidval-webapp](swidval-webapp): A simple, proof of concept webapp that provides a SWID validation service that is deployable to a Java application server.
- [swid-maven-plugin](swid-maven-plugin): An Apache Maven plugin that supports the generation of an XML- or CBOR- based tag as part of a Maven build.
- [swid-repo-client](swid-repo-client): An experimental client that can be used to post a generated SWID tag to the [National Vulnerability Database](https://nvd.nist.gov/) (NVD). The NVD is maintaining a repository of software producer published SWID and CoSWID tags for use in identifying products as part of the NVD's [vulnerability analysis process](https://nvd.nist.gov/general).

