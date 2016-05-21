<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:res="http://decima.nist.gov/xml/assessment-results/0.1"
    xmlns:req="http://decima.nist.gov/xml/requirements/0.1"
    xmlns:swid="http://standards.iso.org/iso/19770/-2/2015/schema.xsd"
    xmlns:decima="http://decima.nist.gov/xsl/extensions"
    exclude-result-prefixes="xs"
    version="2.0">

    <!-- ****************************** -->
    <!-- * Parameters                 * -->
    <!-- ****************************** -->
    <!-- Parameters to tailor the output -->
    <xsl:param name="ignore-outofscope-results" select="true()"/>
    <xsl:param name="xml-output-depth" select="1"/>

    <!-- Parameters for use in calling stylesheets -->
    <xsl:param name="has-requirement-categorizations" select="false()"/>
    
    <xsl:output method="html" doctype-system="about:legacy-compat" encoding="utf-8" indent="yes" />

    <xsl:variable name="requirements" select="document(/res:assessment-results/res:requirements/res:requirement/@href)/req:requirements"/>
    <xsl:variable name="tagdoc" select="document(/res:assessment-results/@subject)"/>
    
    <xsl:key name="requirement-index" match="req:requirement|req:requirement/req:derived-requirements/req:derived-requirement" use="@id" />
    <xsl:key name="resource-index" match="req:resource" use="@id" />
    
    <!-- ****************************** -->
    <!-- * Extension Points           * -->
    <!-- ****************************** -->
    <!-- For use in calling templates to customize output for a specific application -->
    <xsl:template name="process-categorizations"/>
    <xsl:template name="process-header"/>
    <xsl:template name="process-validation-details"/>
    
    <!-- ****************************** -->
    <!-- * Main output template       * -->
    <!-- ****************************** -->
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>SWID Tag Validation Report</title>
                <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
                <style>
                    .row-eq-height {
                    display: -webkit-box;
                    display: -webkit-flex;
                    display: -ms-flexbox;
                    display:         flex;
                    }
                    .row-eq-height > [class*='col-'] {
                    display: flex;
                    flex-direction: column;
                    }
                    .row-eq-height .panel, .row-eq-height .panel-body {
                    height: 100%;
                    }
                    
                    .hiddenRow {
                    padding: 0 !important;
                    }
                    .dl-horizontal.dl-narrow dt {
                    width: 80px;
                    }
                    .dl-horizontal.dl-narrow dd {
                    margin-left: 100px;
                    }
                    .dl-horizontal.dl-wide dt {
                    width: 220px;
                    }
                    .dl-horizontal.dl-wide dd {
                    margin-left: 240px;
                    }
                    .panel-heading .requirement h3 {
                        line-height: 28px;
                    }
                    .panel-heading .requirement .status.label {
                        font-size: 200%;
                    }

                    /* For colorizing XML */
                    code.xml {
                    padding: 0;
                    }
                    .xml-element {
                    color: blue;
                    padding-left: 22px;
                    text-indent: -22px;
                    }
                    .xml-element-omit {
                    padding-left: 22px;
                    text-indent: -22px;
                    }
                    .xml-comment {
                    color: #006400;
                    padding-left: 10px;
                    }
                    .xml-element-end {
                    padding-left: -22px;
                    }
                    .xml-element-name {
                    color: #000096;
                    }
                    .xml-attr {
                    color: #F5844C;
                    }
                    .xml-attr-value {
                    color: #993300;
                    }
                    .xml-text {
                    color: black;
                    }
                </style>
            </head>
            <body>
                <script type="text/javascript" src="bootstrap/js/jquery-1.12.3.min.js"/>
                <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"/>
                <script type="text/javascript" src="bootstrap/js/Chart.min.js"/>
                <script type="text/javascript" src="bootstrap/js/clipboard.min.js"/>
                <script>
                    var clipboard = new Clipboard('.btn');
                    
                    function toggleChevron(e) {
                        $(e.target)
                            .prev('tr.derived-detail-toggle')
                            .find('span.glyphicon')
                            .toggleClass('glyphicon-chevron-up glyphicon-chevron-down');
                    }
                </script>
                <header>
                    <div class="container-fluid">
                        
                        <div id="report" class="page-header">
                            <xsl:call-template name="process-header"/>
                        </div>
                        
                        <div class="row row-eq-height">
                            <div class="col-xs-4">
                                <div class="panel panel-default">
                                    <div class="panel-heading">Validation Details</div>
                                    <div class="panel-body">
                                        <dl class="dl-horizontal dl-narrow">
                                            <dt>Target:</dt>
                                            <dd><xsl:value-of select="res:assessment-results/@subject"/></dd>
                                            <dt>Start:</dt>
                                            <dd><xsl:value-of select="format-dateTime(res:assessment-results/@start,'[MNn] [D1o], [Y0001] [H01]:[m01]:[s01] [z1]')"/></dd>
                                            <dt>End:</dt>
                                            <dd><xsl:value-of select="format-dateTime(res:assessment-results/@end,'[MNn] [D1o], [Y0001] [H01]:[m01]:[s01] [z1]')"/></dd>
                                            <xsl:call-template name="process-validation-details"/>
                                        </dl>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-4">
                                <div class="panel panel-default">
                                    <div class="panel-heading">Validation Overview</div>
                                    <div class="panel-body">
                                        <canvas id="myChart"/>
                                        <script>
                                           
                                            var ctx = $("#myChart");
                                            var myChart = new Chart(ctx, {
                                                type: 'pie',
                                                data: {
                                                    labels: [
                                                        "Pass",
                                                        "Error"
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'WARNING']">, "Warning"</xsl:if>
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_TESTED']">, "Not Tested"</xsl:if>
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'INFORMATIONAL']">, "Informational"</xsl:if>
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_APPLICABLE']">, "Not Applicable"</xsl:if>
                                                    ],
                                                    datasets: [{
                                                        data: [
                                                            <xsl:value-of select="count(//res:base-requirement/res:status[text() = 'PASS'])"/>,
                                                            <xsl:value-of select="count(//res:base-requirement/res:status[text() = 'FAIL'])"/>,
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'WARNING']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'WARNING'])"/>,</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_TESTED']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'NOT_TESTED'])"/>,</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'INFORMATIONAL']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'INFORMATIONAL'])"/>,</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_APPLICABLE']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'NOT_APPLICABLE'])"/>,</xsl:if>
                                                        ],
                                                        backgroundColor: [
                                                            "#5cb85c",
                                                            "#d9534f",
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'WARNING']">"#f0ad4e",</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_TESTED']">"#777",</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'INFORMATIONAL']">"#5bc0de",</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_APPLICABLE']">"#337ab7",</xsl:if>
                                                        ],
                                                    }]
                                                },
                                                options: {
                                                    legend: {
                                                        position: "bottom",
                                                    }
                                                }
                                            });
                                        </script>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-4">
                                <div class="panel panel-default">
                                    <div class="panel-heading">Validation Summary</div>
                                    <div class="panel-body">
                                        <dl class="dl-horizontal dl-wide">
                                            <dt>Total Requirements:</dt>
                                            <dd><xsl:value-of select="count(//res:base-requirement)"/></dd>
                                            <dt>Requirements Checked:</dt>
                                            <dd><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'PASS' or text() = 'FAIL' or text() = 'WARNING'])"/></dd>
                                            <dt>Failed Tests:</dt>
                                            <dd><xsl:value-of select="count(//res:test/res:status[text() = 'FAIL'])"/></dd>
                                        </dl>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>
                <nav>
                    
                </nav>
                <div class="container-fluid">
                    <xsl:apply-templates/>
                </div>
                <footer>
                    
                </footer>
                <script>
                    $('.derived-detail-toggle-target').on('shown.bs.collapse', toggleChevron);
                    $('.derived-detail-toggle-target').on('hidden.bs.collapse', toggleChevron);
                </script>
            </body>
        </html>
    </xsl:template>

    <!-- ****************************** -->
    <!-- * Summary report table       * -->
    <!-- ****************************** -->
    <xsl:template match="res:results">
        <section>
            <div class="row">
                <div class="col-xs-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h1>Validation Result Summary</h1>
                        </div>
                        <div class="panel-body">
                            <table class="table table-condensed table-hover">
                                <thead>
                                    <tr><th>Requirement #</th><th>Summary</th><th>Result</th></tr>
                                </thead>
                                <tbody class="">
                                    <xsl:apply-templates mode="summary"/>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section>
            <div class="row">
                <div class="col-xs-12">
                    <div class="page-header">
                        <h1>Validation Result Details</h1>
                    </div>
                    <xsl:apply-templates mode="detail"/>
                </div>
            </div>
        </section>
    </xsl:template>

    <xsl:template match="res:base-requirement" mode="summary">
        <xsl:if test="not($ignore-outofscope-results) or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <xsl:element name="tr">
                <xsl:if test="res:derived-requirement">
                    <xsl:attribute name="data-toggle">collapse</xsl:attribute>
                    <xsl:attribute name="data-target">.<xsl:value-of select="@id"/>-collapse</xsl:attribute>
                    <xsl:attribute name="class">derived-detail-toggle</xsl:attribute>
                </xsl:if>
                <td><xsl:value-of select="@id"/><xsl:if test="res:derived-requirement">&#160;<span class="glyphicon glyphicon-chevron-up {@id}-icon-toggle" aria-hidden="true"/><span class="sr-only">Toggle related Derived Requirement:</span></xsl:if></td>
                <td><a href="#{$current-req}">
                    <xsl:variable name="key" select="$requirements/key('requirement-index', $current-req)"/>
                    <xsl:choose>
                        <xsl:when test="$key/req:summary">
                            <xsl:value-of select="$key/req:summary/text()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$key/../../req:summary/text()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </a></td><td><xsl:apply-templates select="res:status" mode="status-label"/></td>
            </xsl:element>
            <xsl:apply-templates select="res:derived-requirement" mode="#current"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="res:derived-requirement" mode="summary">
        <xsl:if test="not($ignore-outofscope-results) or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <tr class="collapse out {../@id}-collapse active derived-detail-toggle-target">
                <td><xsl:value-of select="@id"/></td>
                <td><a href="#{$current-req}">
                    <xsl:variable name="key" select="$requirements/key('requirement-index', $current-req)"/>
                    <xsl:choose>
                        <xsl:when test="$key/req:summary">
                            <xsl:value-of select="$key/req:summary/text()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$key/../../req:summary/text()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </a></td><td><xsl:apply-templates select="res:status" mode="status-label"/></td></tr>
            <xsl:apply-templates select="res:derived-requirement" mode="#current"/>
        </xsl:if>
    </xsl:template>

    <!-- use to output the result status by multiple templates -->
    <xsl:template match="res:status" mode="status-label">
        <xsl:variable name="current-req" select="../@id"/>
        <xsl:variable name="key" select="$requirements/key('requirement-index', $current-req)"/>
        <xsl:choose>
            <xsl:when test="text() = 'PASS'"><span class="status label label-success">Pass</span></xsl:when>
            <xsl:when test="text() = 'WARNING'"><span class="status label label-warning">Warning</span></xsl:when>
            <xsl:when test="text() = 'FAIL'"><span class="status label label-danger">Error</span></xsl:when>
            <xsl:when test="text() = 'NOT_TESTED'"><span class="status label label-default">Not Tested</span></xsl:when>
            <xsl:when test="text() = 'NOT_APPLICABLE'"><span class="status label label-default">Not Applicable</span></xsl:when>
            <xsl:when test="text() = 'INFORMATIONAL'"><span class="status label label-info">Informational</span></xsl:when>
            <xsl:otherwise><span class="status label label-default"><xsl:value-of select="text()"/></span></xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ****************************** -->
    <!-- * Requirement details        * -->
    <!-- ****************************** -->
    <xsl:template match="res:base-requirement" mode="detail">
        <xsl:if test="not($ignore-outofscope-results) or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <xsl:variable name="req-id-key" select="$requirements/key('requirement-index', $current-req)"/>
            <xsl:variable name="resource-id-key" select="$requirements/key('resource-index', $current-req)"/>
            <div class="row">
                <div id="{$current-req}" class="col-xs-12 base-req">
                    <div class="panel panel-default">
                        <div class="panel-heading clearfix">
                            <div class="requirement">
                                <div class="pull-right">
                                    <xsl:apply-templates select="res:status" mode="status-label"/>
                                </div>
                                <h2><span class="label label-default"><xsl:value-of select="$current-req"/></span>&#160;<xsl:value-of select="$req-id-key/req:summary/text()"/></h2>
                            </div>
                        </div>
                        <div class="panel-body">
                            <div class="row">
                                <xsl:element name="div">
                                    <xsl:choose>
                                        <xsl:when test="$has-requirement-categorizations">
                                            <xsl:attribute name="class">col-xs-12 col-md-8</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="class">col-xs-12</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <div class="panel">
                                        <div class="panel-heading">
                                            <h3>Requirement</h3>
                                        </div>
                                        <div class="panel-body">
                                            <blockquote>
                                                <p class="req-statement"><xsl:value-of select="$req-id-key/req:statement/text()"/></p>
                                                <footer>Section <xsl:value-of select="$req-id-key/req:reference/@section"/> of <cite title="{$requirements/key('resource-index',$req-id-key/req:reference/@ref)/@name}"><xsl:value-of select="$requirements/key('resource-index',$req-id-key/req:reference/@ref)/@name"/></cite></footer>
                                            </blockquote>
                                        </div>
                                    </div>
                                </xsl:element>
                                <xsl:if test="$has-requirement-categorizations">
                                    <div class="col-xs-12 col-md-4">
                                        <div class="panel">
                                            <div class="panel-heading">
                                                <h3>Categorization</h3>
                                            </div>
                                            <div class="panel-body">
                                                <dl class="dl-horizontal">
                                                    <xsl:call-template name="process-categorizations"/>
                                                </dl>
                                            </div>
                                        </div>
                                    </div>
                                </xsl:if>
                            </div>
                            <xsl:if test="res:derived-requirement">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <table class="table table-condensed table-hover">
                                            <thead>
                                                <tr><th>Derived Requirement #</th><th>Summary</th><th>Result</th></tr>
                                            </thead>
                                            <tbody class="">
                                                <xsl:apply-templates select="res:derived-requirement" mode="detail-derived-table"/>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <xsl:apply-templates select="res:derived-requirement" mode="#current"/>
                            </xsl:if>
                        </div>
                    </div>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="res:derived-requirement" mode="detail-derived-table">
        <xsl:if test="not($ignore-outofscope-results) or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <tr>
                <td><xsl:value-of select="@id"/></td>
                <td><a href="#{$current-req}">
                    <xsl:variable name="key" select="$requirements/key('requirement-index', $current-req)"/>
                    <xsl:choose>
                        <xsl:when test="$key/req:summary">
                            <xsl:value-of select="$key/req:summary/text()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$key/../../req:summary/text()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </a></td><td><xsl:apply-templates select="res:status" mode="status-label"/></td></tr>
        </xsl:if>
    </xsl:template>
        
    <xsl:template match="res:derived-requirement" mode="detail">
        <xsl:if test="not($ignore-outofscope-results) or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <xsl:variable name="req-id-key" select="$requirements/key('requirement-index', $current-req)"/>
            <div class="row">
                <div id="{$current-req}" class="col-xs-12 base-req">
                    <div class="panel panel-default">
                        <div class="panel-heading clearfix">
                            <div class="requirement">
                                <div class="pull-right">
                                    <xsl:apply-templates select="res:status" mode="status-label"/>
                                </div>
                                <h3><span class="label label-default"><xsl:value-of select="$current-req"/></span>&#160;<xsl:choose>
                                        <xsl:when test="$req-id-key/req:summary">
                                            <xsl:value-of select="$req-id-key/req:summary/text()"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="$req-id-key/../../req:summary/text()"/>
                                        </xsl:otherwise>
                                    </xsl:choose></h3>
                            </div>
                        </div>
                        <div class="panel-body">
                            <div class="panel">
                                <div class="panel-heading">
                                    <h4>Requirement</h4>
                                </div>
                                <div class="panel-body">
                                    <blockquote>
                                        <p class="req-statement"><xsl:value-of select="$req-id-key/req:statement/text()"/></p>
                                    </blockquote>
                                </div>
                                <xsl:if test="res:test">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr><th>#</th><th>Status</th><th>Message</th><th>Context (Line/Column)</th></tr>
                                        </thead>
                                        <tbody class="">
                                            <xsl:apply-templates select="res:test" mode="#current"/>
                                        </tbody>
                                    </table>
                                </xsl:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="res:test" mode="detail">
        <tr>
            <td><xsl:number/></td>
            <td><xsl:apply-templates select="res:status" mode="status-label"/></td>
            <td><xsl:value-of select="res:message"/></td>
            <td><xsl:value-of select="res:location/@line"/>:<xsl:value-of select="res:location/@column"/>&#160;<button class="btn btn-default btn-xs" type="button" data-clipboard-text="{res:location/@xpath}">Copy XPath</button></td>
        </tr>
        <tr>
            <td colspan="4">
                <xsl:apply-templates select="$tagdoc" mode="xml-to-html">
                    <xsl:with-param name="xpath" select="res:location/@xpath"/>
                </xsl:apply-templates>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template match="res:status" mode="test-status-label">
        <xsl:variable name="current-req" select="../../@id"/>
        <xsl:variable name="key" select="$requirements/key('requirement-index', $current-req)"/>
        <xsl:choose>
            <xsl:when test="text() = 'PASS'"><span class="status label label-success">Pass</span></xsl:when>
            <xsl:when test="text() = 'WARNING'"><span class="status label label-warning">Warning</span></xsl:when>
            <xsl:when test="text() = 'FAIL'"><span class="status label label-danger">Error</span></xsl:when>
            <xsl:when test="text() = 'NOT_TESTED'"><span class="status label label-default">Not Tested</span></xsl:when>
            <xsl:when test="text() = 'NOT_APPLICABLE'"><span class="status label label-default">Not Applicable</span></xsl:when>
            <xsl:when test="text() = 'INFORMATIONAL'"><span class="status label label-info">Informational</span></xsl:when>
            <xsl:otherwise><span class="status label label-default"><xsl:value-of select="text()"/></span></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- ****************************** -->
    <!-- * Output Tested XML as HTML  * -->
    <!-- ****************************** -->
    <xsl:template match="/" mode="xml-to-html">
        <xsl:param name="xpath"/>
        <code class="xml" >
        <xsl:apply-templates select="decima:evaluate(.,$xpath)" mode="xml-to-html-target"/>
        </code>
    </xsl:template>

    <xsl:template match="*" mode="xml-to-html-target">
        <xsl:if test="parent::*">
            <xsl:apply-templates select="parent::*" mode="xml-to-html-parent-start"/>
            <xsl:if test="position() > 0"><div class="xml-element-omit">...</div></xsl:if>
        </xsl:if>
        
        
        <xsl:apply-templates select="." mode="xml-to-html-target-output">
            <xsl:with-param name="depth" select="$xml-output-depth"/>
        </xsl:apply-templates>
        
        <xsl:if test="following-sibling::*"><div class="xml-element-omit">...</div></xsl:if>

        <xsl:if test="parent::*">
            <xsl:apply-templates select="parent::*" mode="xml-to-html-parent-end"/>
        </xsl:if>
    </xsl:template>
        
    <xsl:template match="*" mode="xml-to-html-parent-start">
        <xsl:if test="parent::*">
            <xsl:apply-templates select="parent::*" mode="xml-to-html-parent-start"/>
            <xsl:if test="position() > 0"><div class="xml-element-omit">...</div></xsl:if>
        </xsl:if>
        <xsl:call-template name="xml-to-html-output-element-start"/>
    </xsl:template>
    
    <xsl:template match="*" mode="xml-to-html-parent-end">
        <xsl:call-template name="xml-to-html-output-element-end"/>
        <xsl:if test="parent::*">
            <xsl:apply-templates select="parent::*" mode="xml-to-html-parent-end"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="*" mode="xml-to-html-target-output">
        <xsl:param name="depth"/>

        <xsl:call-template name="xml-to-html-output-element-start"/>

        <xsl:if test="child::*">
            <xsl:choose>
                <xsl:when test="$depth > 0">
                    <xsl:apply-templates select="child::*" mode="#current">
                        <xsl:with-param name="depth" select="$depth - 1"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <div class="xml-element-omit">...</div>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>

        <xsl:call-template name="xml-to-html-output-element-end"/>
    </xsl:template>
        
    <xsl:template name="xml-to-html-output-element-start">
        <xsl:text disable-output-escaping="yes">&lt;div class="xml-element"&gt;</xsl:text>&lt;<span class="xml-element-name"><xsl:value-of select="name()"/></span>
            <xsl:for-each select="@*">
                <xsl:call-template name="xml-to-html-output-attribute"/>
            </xsl:for-each>
            <xsl:choose>
                <xsl:when test="*|text()|comment()">&gt;</xsl:when>
                <xsl:otherwise>/&gt;<xsl:text disable-output-escaping="yes">&lt;/div&gt;</xsl:text></xsl:otherwise>
            </xsl:choose>
    </xsl:template>

    <xsl:template name="xml-to-html-output-element-end">
        <xsl:if test="*|text()|comment()">
            <div class="xml-element-end">&lt;/<span class="xml-element-name"><xsl:value-of select="name()"/></span>&gt;</div><xsl:text disable-output-escaping="yes">&lt;/div&gt;</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="xml-to-html-output-attribute">
        <span class="xml-attr">&#160;<xsl:value-of select="name()"/><xsl:if test=".">=<span class="xml-attr-value">"<xsl:value-of select="."/>"</span></xsl:if></span>
    </xsl:template>
</xsl:stylesheet>