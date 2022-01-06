Software Identification (SWID) Tag Validator ${project.build.finalName}

Overview:
---------

The SWID Tag Validator is a Java 8 application that checks that a provided SWID
tag conforms to requirements from the ISO/IEC 19770-2:2015 [1] and NIST Internal
Report (NISTIR) 8060 [2].

[1] http://www.iso.org/iso/catalogue_detail?csnumber=65666
[2] https://dx.doi.org/10.6028/NIST.IR.8060


Use:
----

Scripts that support running swidval on *nix an Windows can be found in the bin folder. The application can also be run directly using the "java -jar <SWIDVAL jar>" syntax.

The tool's command line interface can be run using the following arguments:

usage: java -jar <SWIDVAL jar> [options] <swid tag path>
 -A                   the tag is produced by an authoritative creator
                      (default)
 -a                   the tag is not produced by an authoritative creator
 -debug               Enable verbose output
 -h,--help            Display the available cli arguments
 -quiet               Silence console output
 -reportfile <FILE>   the HTML report file to generate (default:
                      validation-report.html)
 -resultfile <FILE>   the result file to write results to (default:
                      validation-result.xml)
 -usecase <arg>       the SWID tag type, which is one of: primary, corpus,
                      patch, or supplemental (default: primary)
 -version             Display the version of the tool

Where:

<SWIDVAL jar> - is the JAR file in lib/${project.groupId}.${project.artifactId}-${project.version}.${project.packaging} of the interpreter distribution.
[options] - is one or more of the command line parameters.
<swid tag path> - is the file path for a SWID tag to validate.

Once the validation is completed, two files will be created:

  validation-result.xml - An XML file containing the set of requirements used
      for validation, and the status of each requirement.
  validation-report.html - A human-readable report based on the validations
      results.


Feedback:
---------

Please send tool defects reports, enhancement requests, and any other related
comments by email to scap@nist.gov.

Changelog:
----------

Version ${project.version}
- Initial release on GitHub.
- Requires use of Java 11+
* Updated several dependencies to resolve security and functionality issues
* Bump junit from 4.12 to 4.13.1 by @dependabot in https://github.com/usnistgov/swid-tools/pull/31
* Bump xmlsec from 1.5.8 to 2.1.7 in /scapval by @dependabot in https://github.com/usnistgov/scapval/pull/32
* Bump Decima from 0.6.4 to 0.7.1

Version 0.6.1
- Added experimental support for Concise Binary Object Representation (CBOR) formatted SWID tags.
- Improved error handling.

Version 0.6.0
- Initial release

