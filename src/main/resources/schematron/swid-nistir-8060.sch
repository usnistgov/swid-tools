<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
    xmlns:swid="http://standards.iso.org/iso/19770/-2/2015/schema.xsd"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://standards.iso.org/iso/19770/-2/2015/schema.xsd swid-schema-2015-06-08.xsd"
    queryBinding="xslt2"
    defaultPhase="swid.primary.auth">
    <title>ISO/IEC 19770-2 SWID Tag Checker based on NISTIR 8060</title>
    <ns uri="http://standards.iso.org/iso/19770/-2/2015/schema.xsd" prefix="swid"/>
    <ns prefix="java" uri="java:gov.nist.decima.swid.schematron"/>

    <phase id="swid.primary.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
    </phase>

    <phase id="swid.primary.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
    </phase>

    <phase id="swid.patch.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
    </phase>

    <phase id="swid.patch.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
    </phase>

    <phase id="swid.corpus.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
    </phase>

    <phase id="swid.corpus.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
    </phase>

    <phase id="swid.supplemental.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
    </phase>

    <phase id="swid.supplemental.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
    </phase>

    <let name="authoritative" value="//swid:Entity[contains(@role,'tagCreator') and (contains(@role,'aggregator') or contains(@role,'distributor') or contains(@role,'licensor') or contains(@role,'softwareCreator'))]"/>
    <let name="type" value="if (/swid:SoftwareIdentity[@patch='true']) then 'patch' else (if (/swid:SoftwareIdentity[@supplemental='true']) then 'supplemental' else (if (/swid:SoftwareIdentity[@corpus='true']) then 'corpus' else 'primary'))"/>

    <pattern id="info">
        <rule id="INFO-1" context="swid:Entity[contains(@role,'tagCreator')]">
            <report id="INFO-1-1" role="info" test="$authoritative">Assessing the tag as authoritative.</report>
            <report id="INFO-1-2" role="info" test="not($authoritative)">Assessing the tag as non-authoritative.</report>
        </rule>
    </pattern>

    <pattern id="ISO-19770-2">
        <rule id="swid-test-software-identity" context="swid:SoftwareIdentity">
        	<assert id="SWID-1-1" test="swid:Entity[contains(@role,'tagCreator')]"/>
        </rule>
        <rule id="swid-test-entity" context="swid:Entity">
            <!-- All Entity elements with the role tagCreator must have the same @regid -->
            <assert id="SWID-2-1" test="not(contains(@role,'tagCreator')) or (every $n in (preceding-sibling::node() union following-sibling::node()) satisfies (not(contains($n/@role,'tagCreator')) or @regid=$n/@regid))"/>
            <!-- Every Entity with the same @regid must have a different @xml:lang -->
        	<assert id="SWID-3-1" test="every $n in (preceding-sibling::node() union following-sibling::node()) satisfies (not(@regid=$n/@regid) or not(lang((ancestor-or-self::*/@xml:lang)[last()],$n)))"/>
	        <!-- Every Entity with the same @regid must have the same @role entries -->
        	<assert id="SWID-4-1" test="every $n in (preceding-sibling::node() union following-sibling::node()) satisfies (not(@regid=$n/@regid) or java:isStringSetEqual(@role,$n/@role,'\s+'))"/>
        </rule>
    </pattern>

    <pattern id="general">
    </pattern>

	<pattern id="general-auth">
    	<rule id="general-software-identity" context="swid:SoftwareIdentity">
    		<assert id="GEN-2-1" test="exists(swid:Entity[contains(@role,'tagCreator') and (contains(@role,'aggregator') or contains(@role,'distributor') or contains(@role,'licensor') or contains(@role,'softwareCreator'))])"/>
    	    <assert id="GEN-3-1" test="swid:Entity[contains(@role,'softwareCreator')]"/>
    	</rule>
<!-- 
    	<rule id="GEN-2" context="swid:SoftwareIdentity">
    	    <assert id="GEN-2-1" test="count(@*[(name() = 'patch' or name() = 'corpus' or name() = 'supplemental') and . = 'true']) &lt;= 1"><value-of select="@patch"/>|<value-of select="@corpus"/>|<value-of select="@supplemental"/></assert>
    	</rule>
        <rule id="GEN-3" context="swid:SoftwareIdentity">
            <title>Requirement GEN-2</title>
    	    <p>Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator, and the @name and @regid attributes are also provided.</p>
            <assert id="GEN-3-1" role="error" test="swid:Entity[contains(@role,'softwareCreator')]">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator.</assert>
            <assert id="GEN-3-2" role="error" test="swid:Entity[contains(@role,'softwareCreator')]/@name">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator and the @name attribute is also provided.</assert>
            <assert id="GEN-3-3" role="error" test="swid:Entity[contains(@role,'softwareCreator')]/@regid">Authoritative tag creators MUST provide an &lt;Entity&gt; element where the @role attribute contains the value softwareCreator and the @regid attribute is also provided.</assert>
        </rule>
 -->
	    <rule id="general-auth-entity-softwareCreator" context="swid:Entity[contains(@role,'softwareCreator')]">
            <assert id="GEN-3-2" test="@regid"/>
        </rule>
	</pattern>

	<pattern id="general-non-auth">
	</pattern>
</schema>