<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
    xmlns:swid="http://standards.iso.org/iso/19770/-2/2015/schema.xsd"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://standards.iso.org/iso/19770/-2/2015/schema.xsd swid-schema-2015-06-08.xsd"
    queryBinding="xslt">
    <title>ISO/IEC 19770-2 SWID Tag Checker based on NISTIR 8060</title>
    <ns uri="http://standards.iso.org/iso/19770/-2/2015/schema.xsd" prefix="swid"/>
    
    <phase id="swid.auth">
        <active pattern="test-auth"/>
        <active pattern="gen-2"/>
    </phase>

    <phase id="swid.nonauth">
        <active pattern="test-auth"/>
        <active pattern="gen-2"/>
    </phase>

    <let name="authoritative" value="//swid:Entity[contains(@role,'tagCreator') and (contains(@role,'aggregator') or contains(@role,'distributor') or contains(@role,'licensor') or contains(@role,'softwareCreator'))]"/>

    <pattern id="test-auth">
        <rule context="swid:Entity[contains(@role,'tagCreator')]">
            <report role="info" test="$authoritative">The tag is authoritative.</report>
            <report role="info" test="not($authoritative)">The tag is non-authoritative.</report>
        </rule>
    </pattern>

    <pattern id="blah">
        <rule id="gen-2" context="swid:SoftwareIdentity">
	        <title>Requirement GEN-2</title>
    	    <p>Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator, and the @name and @regid attributes are also provided.</p>
            <assert id="GEN-2-1" role="error" test="swid:Entity[contains(@role,'softwareCreator')]">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator.</assert>
        </rule>
        <rule id="GEN-2-2" context="swid:Entity[contains(@role,'softwareCreator')]">
            <assert id="GEN-2-2" role="error" test="@name">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator and the @name attribute is also provided.</assert>
            <assert id="GEN-2-3" role="error" test="@regid">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator and the @regid attribute is also provided.</assert>
        </rule>
    </pattern>
</schema>