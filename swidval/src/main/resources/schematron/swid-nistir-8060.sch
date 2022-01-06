<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://purl.oclc.org/dsdl/schematron"
    xmlns:swid="http://standards.iso.org/iso/19770/-2/2015/schema.xsd"
    xmlns:n8060="http://csrc.nist.gov/ns/swid/2015-extensions/1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://standards.iso.org/iso/19770/-2/2015/schema.xsd swid-schema-2015-06-08.xsd http://csrc.nist.gov/ns/swid/2015-extensions/1.0 http://csrc.nist.gov/schema/swid/2015-extensions/swid-2015-extensions-1.0.xsd"
    queryBinding="xslt2"
    defaultPhase="swid.primary.auth">
    <title>ISO/IEC 19770-2 SWID Tag Checker based on NISTIR 8060</title>
    <ns uri="http://standards.iso.org/iso/19770/-2/2015/schema.xsd" prefix="swid"/>
    <ns uri="http://csrc.nist.gov/ns/swid/2015-extensions/1.0" prefix="n8060"/>
    <ns prefix="java" uri="java:gov.nist.secauto.swid.swidval.schematron"/>

    <phase id="swid.primary.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
        <active pattern="primary"/>
        <active pattern="primary-auth"/>
    </phase>

    <phase id="swid.primary.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
        <active pattern="primary"/>
        <active pattern="primary-non-auth"/>
    </phase>

    <phase id="swid.patch.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
        <active pattern="patch"/>
        <active pattern="patch-auth"/>
    </phase>

    <phase id="swid.patch.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
        <active pattern="patch"/>
        <active pattern="patch-non-auth"/>
    </phase>

    <phase id="swid.corpus.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
        <active pattern="corpus"/>
    </phase>

    <phase id="swid.corpus.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
        <active pattern="corpus"/>
    </phase>

    <phase id="swid.supplemental.auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-auth"/>
        <active pattern="supplemental"/>
    </phase>

    <phase id="swid.supplemental.non-auth">
        <active pattern="info"/>
        <active pattern="ISO-19770-2"/>
        <active pattern="general"/>
        <active pattern="general-non-auth"/>
        <active pattern="supplemental"/>
    </phase>

    <let name="authoritative" value="if (//swid:Entity[contains(@role,'tagCreator') and (contains(@role,'aggregator') or contains(@role,'distributor') or contains(@role,'licensor') or contains(@role,'softwareCreator'))]) then 'true' else 'false'"/>
    <let name="type" value="if (/swid:SoftwareIdentity[@patch='true']) then 'patch' else (if (/swid:SoftwareIdentity[@supplemental='true']) then 'supplemental' else (if (/swid:SoftwareIdentity[@corpus='true']) then 'corpus' else 'primary'))"/>

    <pattern id="info">
        <rule id="INFO-1" context="swid:SoftwareIdentity">
            <report id="INFO-1-1" role="info" test="true()">Assessing the tag as: <value-of select="if ($authoritative = 'true') then 'authoritative' else 'non-authoritative'"/>.</report>
            <report id="INFO-1-2" role="info" test="true()">Assessing the tag type as: <value-of select="$type"/>.</report>
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
            <!--
        	<assert id="SWID-3-1" test="every $n in (preceding-sibling::node() union following-sibling::node()) satisfies (not(@regid=$n/@regid) or not(lang((ancestor-or-self::*/@xml:lang)[last()],$n)))"/>
        	-->
        </rule>
    </pattern>

    <pattern id="general">
        <rule id="general-software-identity" context="swid:SoftwareIdentity">
            <assert id="GEN-2-1" test="@xml:lang"/>
            <assert id="GEN-2-2" test="@xml:lang and string-length(@xml:lang)!=0"/>
        </rule>
        <rule id="general-entity" context="swid:Entity">
            <!-- Every Entity with the same @regid must have the same @role entries -->
            <assert id="GEN-7-1" test="every $n in (preceding-sibling::node() union following-sibling::node()) satisfies (not(@regid=$n/@regid) or @role=$n/@role)"><value-of select="@name"/></assert>
        </rule>
        <rule id="general-link" context="swid:Link">
            <assert id="GEN-11-1" test="not(matches(@href,'^swid:')) or matches(@href,'^([a-z0-9+.-]+):(//((([a-z0-9\-._~!$&amp;''()*+,;=:]|%[0-9A-F]{2})*)@)?(([a-z0-9\-._~!$&amp;''()*+,;=]|%[0-9A-F]{2})*)(:(\d*))?(/([a-z0-9\-._~!$&amp;''()*+,;=:@/]|%[0-9A-F]{2})*)?|(/?([a-z0-9\-._~!$&amp;''()*+,;=:@]|%[0-9A-F]{2})+([a-z0-9\-._~!$&amp;''()*+,;=:@/]|%[0-9A-F]{2})*)?)(\?(([a-z0-9\-._~!$&amp;''()*+,;=:/?@]|%[0-9A-F]{2})*))?(#(([a-z0-9\-._~!$&amp;''()*+,;=:/?@]|%[0-9A-F]{2})*))?$')"><value-of select="@href"/></assert>
        </rule>
        <rule id="general-payload-and-evidence" context="swid:Payload|swid:Evidence">
            <assert id="GEN-22-1" test="@n8060:pathSeparator" ><value-of select="local-name()"/></assert>
            <assert id="GEN-22-2" test="not(@n8060:pathSeparator) or string-length(@n8060:pathSeparator)!=0" ><value-of select="local-name()"/>|<value-of select="@n8060:pathSeparator/name()"/></assert>
            <assert id="GEN-23-1" test="@n8060:envVarPrefix" ><value-of select="local-name()"/></assert>
            <assert id="GEN-23-2" test="not(@n8060:envVarPrefix) or string-length(@n8060:envVarPrefix)!=0" ><value-of select="local-name()"/>|<value-of select="@n8060:envVarPrefix/name()"/></assert>
            <assert id="GEN-24-1" test="@n8060:envVarSuffix" ><value-of select="local-name()"/></assert>
            <assert id="GEN-24-2" test="not(@n8060:envVarSuffix) or string-length(@n8060:envVarSuffix)!=0" ><value-of select="local-name()"/>|<value-of select="@n8060:envVarSuffix/name()"/></assert>
        </rule>

        <rule id="general-file" context="swid:File">
            <assert id="GEN-14-1" test="@n8060:mutable='true' or @size"><value-of select="@name"/></assert>
            <assert id="GEN-14-2" test="@n8060:mutable='true' or (@size and string-length(@size)!=0)"><value-of select="@name"/></assert>
            <assert id="GEN-15-1" test="@n8060:mutable='true' or @version"><value-of select="@name"/></assert>
            <assert id="GEN-15-2" test="not(@version) or string-length(@version)!=0"><value-of select="@name"/></assert>
            <assert id="GEN-18-1" test="not(@*:hash[namespace-uri() = 'http://www.w3.org/2001/04/xmldsig-more#md5'])"><value-of select="@name"/></assert>
            <assert id="GEN-18-2" test="not(@*:hash[namespace-uri() = 'http://www.w3.org/2000/09/xmldsig#sha1'])"><value-of select="@name"/></assert>
            <assert id="GEN-18-3" test="not(@*:hash[namespace-uri() = 'http://www.w3.org/2001/04/xmldsig-more#sha224'])"><value-of select="@name"/></assert>
            <assert id="GEN-18-4" test="not(@*:hash[namespace-uri() = 'http://www.w3.org/2001/04/xmlenc#ripemd160'])"><value-of select="@name"/></assert>
        </rule>
    </pattern>

	<pattern id="general-auth">
    	<rule id="general-auth-software-identity" context="swid:SoftwareIdentity">
    		<assert id="GEN-8-1" test="swid:Entity[contains(@role,'tagCreator') and (contains(@role,'aggregator') or contains(@role,'distributor') or contains(@role,'licensor') or contains(@role,'softwareCreator'))]"/>
    	    <assert id="GEN-9-1" test="swid:Entity[contains(@role,'softwareCreator')]"/>
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
	    <rule id="general-auth-entity" context="swid:Entity">
	        <assert id="GEN-4-1" test="@regid"><value-of select="@name"/></assert>
	        <assert id="GEN-4-2" test="@regid and string-length(@regid)!=0"><value-of select="@name"/></assert>
	        <assert id="GEN-4-3" test="@regid and @regid!='http://invalid.unavailable'"><value-of select="@name"/></assert>
	    </rule>
<!--
	    <rule id="general-auth-entity-softwareCreator" context="swid:Entity[contains(@role,'softwareCreator')]">
            <assert id="GEN-3-2" test="@regid"/>
        </rule>
 -->
	    <rule id="general-auth-file" context="swid:Payload//swid:File">
	        <assert id="GEN-16-1" test="@n8060:mutable='true' or @*:hash"><value-of select="@name"/></assert>
<!--	        <assert id="GEN-16-2" subject="@*:hash" test="matches(text(),'^[a-fA-F0-9]+$')"><value-of select="@name"/>|<value-of select="local-name(@*:hash)"/>|<value-of select="namespace-uri(@*:hash)"/></assert>
-->
	        <assert id="GEN-19-1" test="@n8060:mutable='true' or @*:hash[namespace-uri() = 'http://www.w3.org/2001/04/xmlenc#sha256']"><value-of select="@name"/></assert>
	        <assert id="GEN-19-2" test="@n8060:mutable='true' or matches(@*:hash[namespace-uri() = 'http://www.w3.org/2001/04/xmlenc#sha256'], '^[abcdef0-9]{64}$','i')"><value-of select="@name"/></assert>
	    </rule>
	</pattern>

	<pattern id="general-non-auth">
	    <rule id="general-non-auth-software-identity" context="swid:SoftwareIdentity">
	        <assert id="GEN-10-1" test="swid:Entity[contains(@role,'softwareCreator')]"/>
	    </rule>
	    <rule id="general-non-auth-entity" context="swid:Entity">
	        <assert id="GEN-5-1" test="not(contains(@role,'tagCreator')) or @regid"><value-of select="@name"/></assert>
	        <assert id="GEN-5-2" test="not(contains(@role,'tagCreator')) or @regid and string-length(@regid)!=0"><value-of select="@name"/></assert>
	        <assert id="GEN-5-3" test="not(contains(@role,'tagCreator')) or @regid and @regid!='http://invalid.unavailable'"><value-of select="@name"/></assert>
	        <assert id="GEN-6-1" test="@regid"><value-of select="@name"/></assert>
	        <assert id="GEN-6-2" test="@regid and string-length(@regid)!=0"><value-of select="@name"/></assert>
	        <assert id="GEN-6-3" test="@regid and @regid!='http://invalid.unavailable'"><value-of select="@name"/></assert>
	    </rule>

	    <rule id="general-non-auth-file" context="swid:Evidence//swid:File">
	        <assert id="GEN-17-1" test="@*:hash"><value-of select="@name"/></assert>
	        <assert id="GEN-20-1" test="@*:hash[namespace-uri() = 'http://www.w3.org/2001/04/xmlenc#sha256']"><value-of select="@name"/></assert>
	    </rule>
	</pattern>
    
    <pattern id="corpus">
        <rule id="corpus-software-identity" context="swid:SoftwareIdentity">
            <assert id="COR-1-1" test="exists(@corpus) and @corpus = true()"/>
            <assert id="COR-1-2" test="empty(@patch) or @patch = false()"/>
            <assert id="COR-1-3" test="empty(@supplemental) or @supplemental = false()"/>
            <assert id="COR-2-1" test="@version"><value-of select="@name"/></assert>
            <assert id="COR-2-2" test="not(@version) or string-length(@version)!=0"/>
            <assert id="COR-3-1" test="not(@version) or @versionScheme"/>
            <assert id="COR-3-2" test="not(@versionScheme) or string-length(@versionScheme)!=0"/>
            <assert id="COR-4-1" test="swid:Payload"/>
            <assert id="COR-4-2" test="not(swid:Evidence)"/>
        </rule>
    </pattern>
    
    <pattern id="primary">
        <rule id="primary-software-identity" context="swid:SoftwareIdentity">
            <assert id="PRI-1-1" test="empty(@corpus) or @corpus = false()"/>
            <assert id="PRI-1-2" test="empty(@patch) or @patch = false()"/>
            <assert id="PRI-1-3" test="empty(@supplemental) or @supplemental = false()"/>
            <assert id="PRI-13-1" test="swid:Meta"/>
        </rule>

        <rule id="primary-meta" context="swid:Meta">
            <assert id="PRI-13-2" test="@product"/>
            <assert id="PRI-13-3" test="@colloquialVersion"/>
            <assert id="PRI-13-4" test="@revision"/>
            <assert id="PRI-13-5" test="@edition"/>
        </rule>
            
        <rule id="primary-payload-or-evidence" context="swid:Payload|swid:Evidence">
            <assert id="PRI-8-1" test="exists(descendant::swid:File)"/>
        </rule>
    </pattern>
    
    <pattern id="primary-auth">
        <rule id="primary-auth-software-identity" context="swid:SoftwareIdentity">
            <assert id="PRI-2-1" test="@version"><value-of select="@name"/></assert>
            <assert id="PRI-2-2" test="not(@version) or string-length(@version)!=0"/>
            <assert id="PRI-3-1" test="not(@version) or @versionScheme"/>
            <assert id="PRI-3-2" test="not(@versionScheme) or string-length(@versionScheme)!=0"/>
            <assert id="PRI-6-1" test="swid:Payload"/>
            <assert id="PRI-6-2" test="not(swid:Evidence)"/>
        </rule>
    </pattern>
    
    <pattern id="primary-non-auth">
        <rule id="primary-non-auth-software-identity" context="swid:SoftwareIdentity">
            <assert id="PRI-4-1" test="@version"><value-of select="@name"/></assert>
            <assert id="PRI-4-2" test="not(@version) or string-length(@version)!=0"/>
            <assert id="PRI-5-1" test="not(@version) or @versionScheme"/>
            <assert id="PRI-5-2" test="not(@versionScheme) or string-length(@versionScheme)!=0"/>
            <assert id="PRI-7-1" test="swid:Evidence"/>
            <assert id="PRI-7-2" test="not(swid:Payload)"/>
        </rule>
    </pattern>

    <pattern id="patch">
        <rule id="patch-software-identity" context="swid:SoftwareIdentity">
            <assert id="PAT-1-1" test="empty(@corpus) or @corpus = false()"/>
            <assert id="PAT-1-2" test="exists(@patch) and @patch = true()"/>
            <assert id="PAT-1-3" test="empty(@supplemental) or @supplemental = false()"/>
        </rule>
    </pattern>
    
    <pattern id="patch-auth">
        <rule id="patch-auth-file" context="swid:Payload//swid:File">
            <assert id="PAT-3-1" test="exists(@n8060:patchEvent) and (@n8060:patchEvent = 'add' or @n8060:patchEvent = 'modify' or @n8060:patchEvent = 'remove')"/>
        </rule>
    </pattern>
    
    <pattern id="patch-non-auth">
        <rule id="patch-non-auth-evidence" context="swid:Evidence">
            <assert id="PAT-4-1" test="exists(child::swid:File)"/>
        </rule>
    </pattern>
    
    <pattern id="supplemental">
        <rule id="supplemental-software-identity" context="swid:SoftwareIdentity">
            <assert id="SUP-1-1" test="empty(@corpus) or @corpus = false()"/>
            <assert id="SUP-1-2" test="empty(@patch) or @patch = false()"/>
            <assert id="SUP-1-3" test="exists(@supplemental) and @supplemental = true()"/>
            <assert id="SUP-2-1" test="swid:Link[@rel = 'supplemental']"/>
        </rule>

        <rule id="supplemental-link-supplemental" context="swid:Link[@rel = 'supplemental']">
            <assert id="SUP-2-2" test="exists(@href) and (starts-with(@href, 'swid:') or starts-with(@href, 'swidpath:'))" />
        </rule>    
    </pattern>
</schema>