<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
    xmlns:swid="http://standards.iso.org/iso/19770/-2/2015/schema.xsd"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://standards.iso.org/iso/19770/-2/2015/schema.xsd swid-schema-2015-06-08.xsd"
    queryBinding="xslt">
    <title>ISO/IEC 19770-2 SWID Tag Checker based on NISTIR 8060</title>
    <ns uri="http://standards.iso.org/iso/19770/-2/2015/schema.xsd" prefix="swid"/>
    
    <phase id="swid.auth">
        <active pattern="info"/>
        <active pattern="gen-2"/>
    </phase>

    <phase id="swid.nonauth">
        <active pattern="info"/>
        <active pattern="gen-2"/>
    </phase>

    <let name="authoritative" value="//swid:Entity[contains(@role,'tagCreator') and (contains(@role,'aggregator') or contains(@role,'distributor') or contains(@role,'licensor') or contains(@role,'softwareCreator'))]"/>

    <pattern id="info">
        <rule id="INFO-1" context="swid:Entity[contains(@role,'tagCreator')]">
            <report id="INFO-1-1" role="info" test="$authoritative">The tag is authoritative.</report>
            <report id="INFO-1-2" role="info" test="not($authoritative)">The tag is non-authoritative.</report>
        </rule>
    </pattern>

    <pattern id="general">
    	<rule id="GEN-2" context="swid:SoftwareIdentity">
    	    <assert id="GEN-2-1" test="count(@*[(name() = 'patch' or name() = 'corpus' or name() = 'supplemental') and . = 'true']) &lt;= 1"><value-of select="@patch"/>|<value-of select="@corpus"/>|<value-of select="@supplemental"/></assert>
    	</rule>
        <rule id="GEN-3" context="swid:SoftwareIdentity">
<!--
            <title>Requirement GEN-2</title>
    	    <p>Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator, and the @name and @regid attributes are also provided.</p>
-->
            <assert id="GEN-3-1" role="error" test="swid:Entity[contains(@role,'softwareCreator')]">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator.</assert>
            <assert id="GEN-3-2" role="error" test="swid:Entity[contains(@role,'softwareCreator')]/@name">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator and the @name attribute is also provided.</assert>
            <assert id="GEN-3-3" role="error" test="swid:Entity[contains(@role,'softwareCreator')]/@regid">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator and the @regid attribute is also provided.</assert>
        </rule>
    </pattern>
</schema>