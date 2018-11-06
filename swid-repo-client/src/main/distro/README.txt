Software Identification (SWID) Tag Client ${project.build.finalName}

Overview:
---------

The SWID Client is a Java 8 application that allows a user to access SWID services to post SWID tags.

[1] http://www.iso.org/iso/catalogue_detail?csnumber=65666
[2] https://dx.doi.org/10.6028/NIST.IR.8060


Use:
----

The tool's command line interface can be run using the following arguments:

usage: java -jar <SWIDCLIENT jar> [options] <swid tag path>

    --help
    --keystore-file <arg>     the client keystore file path
    --keystore-passwd <arg>   the client keystore password
    --seed <arg>              the client seed
    --tag-type <arg>          the swid tag type - corpus, primary, patch,
                              supplemental
    --update                  the action requested. If not present, client will use the default action to insert SWID

Where:

<SWIDVAL jar> - is the name of the ${project.build.finalName}.${project.packaging} JAR file
    contained in the parent directory of the interpreter distribution.
[options] - is one or more of the command line parameters.
<swid tag path> - is the file path for a SWID tag to validate.

Once the insert or update process is completed, the SWID tags can be viewed by accessing the URLs listed on the response

Feedback:
---------

Please send tool defects reports, enhancement requests, and any other related
comments by email to nvd@nist.gov.

Changelog:
----------

Version ${project.version}
- Initial release

