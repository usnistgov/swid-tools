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
        <dt class="col-sm-8">Tag Type:</dt>
        <dd class="col-sm-4"><xsl:value-of select="res:assessment-results/res:properties/res:property[@name='tag-type']"/></dd>
        <dt class="col-sm-8">Authoritative Tag:</dt>
        <dd class="col-sm-4"><xsl:value-of select="res:assessment-results/res:properties/res:property[@name='authoritative']"/></dd>
    </xsl:template>

    <xsl:template name="process-categorizations">
        <xsl:variable name="current-req" select="@id"/>
        <xsl:variable name="req-id-key" select="$requirements/key('requirement-index', $current-req)"/>

        <dt class="col-sm-7">Applicable Tag Type:</dt>
        <dd class="col-sm-5 text-capitalize"><xsl:value-of select="$req-id-key/@swid-ext:tag-type"/></dd>

        <dt class="col-sm-7">Producer Scope:</dt>
        <dd class="col-sm-5 text-capitalize" data-toggle="tooltip">
            <xsl:choose>
                <xsl:when test="$req-id-key/@swid-ext:scope='all'"><xsl:attribute name="title">Applies to both authoritative and non-authoritative tags.</xsl:attribute></xsl:when>
            </xsl:choose>
            <xsl:value-of select="$req-id-key/@swid-ext:scope"/>
        </dd>
        
        <dt class="col-sm-7">Guideline Category:</dt>
        <dd class="col-sm-5 text-capitalize"><xsl:value-of select="$req-id-key/@swid-ext:category"/></dd>
    </xsl:template>
</xsl:stylesheet>