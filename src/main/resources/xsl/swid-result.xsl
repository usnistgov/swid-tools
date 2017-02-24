<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:res="http://csrc.nist.gov/ns/decima/results/1.0"
    xmlns:swid-ext="https://csrc.nist.gov/ns/swidval/swid-requirements-ext/0.1"
    exclude-result-prefixes="#all"
    version="2.0">

    <xsl:import href="classpath:xsl/result.xsl"/>
    
    <xsl:param name="has-requirement-categorizations" select="true()"/>
    <xsl:template name="process-header">
    	<h1>SWID Tag <small>Validation Report</small></h1>
    </xsl:template>

    <xsl:template name="process-validation-summary">
        <dt>Tag Type:</dt>
        <dd><xsl:value-of select="res:assessment-results/res:properties/res:property[@name='tag-type']"/></dd>
        <dt>Authoritative Tag:</dt>
        <dd><xsl:value-of select="res:assessment-results/res:properties/res:property[@name='authoritative']"/></dd>
    </xsl:template>

    <xsl:template name="process-categorizations">
        <xsl:variable name="current-req" select="@id"/>
        <xsl:variable name="req-id-key" select="$requirements/key('requirement-index', $current-req)"/>

        <dt>Applicable Tag Type:</dt>
        <dd class="text-capitalize"><xsl:value-of select="$req-id-key/@swid-ext:tag-type"/></dd>

        <dt>Producer Scope:</dt>
        <dd class="text-capitalize"><xsl:value-of select="$req-id-key/@swid-ext:scope"/></dd>
        
        <dt>Guideline Category:</dt>
        <dd class="text-capitalize"><xsl:value-of select="$req-id-key/@swid-ext:category"/></dd>
    </xsl:template>
</xsl:stylesheet>