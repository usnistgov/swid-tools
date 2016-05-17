<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:res="http://decima.nist.gov/xml/assessment-results/0.1"
    xmlns:req="http://decima.nist.gov/xml/requirements/0.1"
    xmlns:swid-ext="http://decima.nist.gov/xml/swid-requirements-ext/0.1"
    exclude-result-prefixes="xs"
    version="2.0">

    <xsl:import href="result.xsl"/>
    
    <xsl:param name="has-requirement-categorizations" select="true()"/>
    
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