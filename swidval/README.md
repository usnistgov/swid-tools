# Software Identification (SWID) Tag Validation Tool

This submodule is the home of the SWID Tag Validation tool, which validates a SWID tag for conformance with ISO/IEC 19770-2:2015 and the guidance in NIST [Internal Report 8060][nistir-8060].

This project is implemented using the [Decima Framework][decima], which provides the validation capabilities used to perform the SWID tag validation.

A binary distribution of this tool can be found on the NIST [SWID tools page][nist-swid-tools].

This tool can also be [used as an API][swidval-api].

The source for this tool is found in this repo and can be built using Apache Maven as follows:

```bash
cd ..
mvn clean install
```

[nistir-8060]: https://csrc.nist.gov/publications/detail/nistir/8060/final
[nist-swid-tools]: https://csrc.nist.gov/Projects/Software-Identification-SWID/resources
[decima]: https://pages.nist.gov/decima/
[swidval-api]: https://pages.nist.gov/swid-tools/swidval/
