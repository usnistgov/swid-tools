<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:res="http://decima.nist.gov/xml/assessment-results/0.1"
    xmlns:req="http://decima.nist.gov/xml/requirements/0.1"
    exclude-result-prefixes="xs"
    version="2.0">

    <xsl:param name="output-summary-outofscope" select="false()"/>
    <xsl:param name="has-requirement-categorizations" select="false()"/>

    <xsl:output method="html" doctype-system="about:legacy-compat" encoding="utf-8" indent="yes" />

    <xsl:variable name="requirements" select="document(/res:assessment-results/res:requirements/res:requirement/@href)/req:requirements"/>

    <xsl:key name="requirement-index" match="req:requirement|req:requirement/req:derived-requirements/req:derived-requirement" use="@id" />
    <xsl:key name="resource-index" match="req:resource" use="@id" />
    
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
                    /*
                    .base-req > .panel-default {
                    border-color: #eee;
                    }
                    .base-req > .panel-default > .panel-heading {
                    color: #333333;
                    background-color: #eee;
                    border-color: #eee;
                    }
                    .base-req > .panel-default > .panel-heading + .panel-collapse > .panel-body {
                    border-top-color: #eee;
                    }
                    .base-req > .panel-default > .panel-heading .badge {
                    color: #f5f5f5;
                    background-color: #333333;
                    }
                    .base-req > .panel-default > .panel-footer + .panel-collapse > .panel-body {
                    border-bottom-color: #eee;
                    }
                    */
                </style>
            </head>
            <body>
                <script type="text/javascript" src="bootstrap/js/jquery-1.12.3.min.js"/>
                <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"/>
                <script type="text/javascript" src="bootstrap/js/Chart.min.js"/>
                <header>
                    <div class="container-fluid">
                        
                        <div id="report" class="page-header">
                            <h1>SWID Tag <small>Validation Report</small></h1>
                        </div>
                        
                        <div class="row row-eq-height">
                            <div class="col-xs-4">
                                <div class="panel panel-default">
                                    <div class="panel-heading">Validation Details</div>
                                    <div class="panel-body">
                                        <dl class="dl-horizontal dl-narrow">
                                            <dt>Target Tag:</dt>
                                            <dd><xsl:value-of select="res:assessment-results/@subject"/></dd>
                                            <dt>Start:</dt>
                                            <dd><xsl:value-of select="format-dateTime(res:assessment-results/@start,'[H01]:[m01]:[s01] [z1] on [MNn] [D1o], [Y0001]')"/></dd>
                                            <dt>End:</dt>
                                            <dd><xsl:value-of select="format-dateTime(res:assessment-results/@end,'[H01]:[m01]:[s01] [z1] on [MNn] [D1o], [Y0001]')"/></dd>
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
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'FAIL' and @severity = 'WARNING']">, "Warning"</xsl:if>
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_TESTED']">, "Not Tested"</xsl:if>
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'INFORMATIONAL']">, "Informational"</xsl:if>
                                                        <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_APPLICABLE']">, "Not Applicable"</xsl:if>
                                                    ],
                                                    datasets: [{
                                                        data: [
                                                            <xsl:value-of select="count(//res:base-requirement/res:status[text() = 'PASS'])"/>,
                                                            <xsl:value-of select="count(//res:base-requirement/res:status[text() = 'FAIL' and @severity = 'ERROR'])"/>,
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'FAIL' and @severity = 'WARNING']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'FAIL' and @severity = 'WARNING'])"/>,</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_TESTED']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'NOT_TESTED'])"/>,</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'INFORMATIONAL']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'INFORMATIONAL'])"/>,</xsl:if>
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'NOT_APPLICABLE']"><xsl:value-of select="count(//res:base-requirement/res:status[text() = 'NOT_APPLICABLE'])"/>,</xsl:if>
                                                        ],
                                                        backgroundColor: [
                                                            "#5cb85c",
                                                            "#d9534f",
                                                            <xsl:if test="//res:base-requirement/res:status[text() = 'FAIL' and @severity = 'WARNING']">"#f0ad4e",</xsl:if>
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
                                        Text
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
            </body>
        </html>
    </xsl:template>

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
        <xsl:if test="$output-summary-outofscope or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <xsl:element name="tr">
                <xsl:if test="res:derived-requirement">
                    <xsl:attribute name="data-toggle">collapse</xsl:attribute>
                    <xsl:attribute name="data-target">.<xsl:value-of select="@id"/>-collapse</xsl:attribute>
                    <xsl:attribute name="class">accordion-toggle</xsl:attribute>
                </xsl:if>
                <td><xsl:value-of select="@id"/><xsl:if test="res:derived-requirement"><span class="caret"></span></xsl:if></td>
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
        <xsl:if test="$output-summary-outofscope or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <tr class="collapse out {../@id}-collapse">
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

    <xsl:template match="res:status" mode="status-label">
        <xsl:variable name="current-req" select="../@id"/>
        <xsl:variable name="key" select="$requirements/key('requirement-index', $current-req)"/>
        <xsl:choose>
            <xsl:when test="text() = 'PASS'"><span class="label label-success">Pass</span></xsl:when>
            <xsl:when test="text() = 'FAIL'">
                <xsl:choose>
                    <xsl:when test="@severity = 'ERROR'"><span class="label label-danger">Error</span></xsl:when>
                    <xsl:when test="@severity = 'WARNING'"><span class="label label-warning">Warning</span></xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="text() = 'NOT_TESTED'"><span class="label label-default">Not Tested</span></xsl:when>
            <xsl:when test="text() = 'NOT_APPLICABLE'"><span class="label label-default">Not Applicable</span></xsl:when>
            <xsl:when test="text() = 'INFORMATIONAL'"><span class="label label-info">Informational</span></xsl:when>
            <xsl:otherwise><xsl:value-of select="text()"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="res:base-requirement" mode="detail">
        <xsl:if test="$output-summary-outofscope or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <xsl:variable name="req-id-key" select="$requirements/key('requirement-index', $current-req)"/>
            <xsl:variable name="resource-id-key" select="$requirements/key('resource-index', $current-req)"/>
            <div class="row">
                <div id="{$current-req}" class="col-xs-12 base-req">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h2><span class="label label-default"><xsl:value-of select="$current-req"/></span>&#160;<xsl:value-of select="$req-id-key/req:summary/text()"/>
                                <div class="pull-right">
                                    <xsl:apply-templates select="res:status" mode="status-label"/>
                                </div>
                            </h2>
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
                                            <p class="req-statement"><xsl:value-of select="$req-id-key/req:statement/text()"/></p>
                                            <p><em>Reference: <xsl:value-of select="$requirements/key('resource-index',$req-id-key/req:reference/@ref)/@name"/> Section <xsl:value-of select="$req-id-key/req:reference/@section"/></em></p>
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

    <xsl:template name="process-categorizations"/>

    <xsl:template match="res:derived-requirement" mode="detail-derived-table">
        <xsl:if test="$output-summary-outofscope or res:status/text() != 'NOT_IN_SCOPE'">
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
        <xsl:if test="$output-summary-outofscope or res:status/text() != 'NOT_IN_SCOPE'">
            <xsl:variable name="current-req" select="@id"/>
            <xsl:variable name="req-id-key" select="$requirements/key('requirement-index', $current-req)"/>
            <div class="row">
                <div id="{$current-req}" class="col-xs-12 base-req">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3><span class="label label-default"><xsl:value-of select="$current-req"/></span>&#160;
                                <xsl:choose>
                                    <xsl:when test="$req-id-key/req:summary">
                                        <xsl:value-of select="$req-id-key/req:summary/text()"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="$req-id-key/../../req:summary/text()"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <div class="pull-right">
                                    <xsl:apply-templates select="res:status" mode="status-label"/>
                                </div>
                            </h3>
                        </div>
                        <div class="panel-body">
                            <div class="panel">
                                <div class="panel-heading">
                                    <h3>Requirement</h3>
                                </div>
                                <div class="panel-body">
                                    <p class="req-statement"><xsl:value-of select="$req-id-key/req:statement/text()"/></p>
                                </div>
                                <xsl:if test="res:test">
                                    <table class="table">
                                        <thead>
                                            <tr><th>#</th><th>Status</th><th>Message</th><th>Context</th></tr>
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
            <th>Message</th>
            <th>Context</th>
        </tr>
    </xsl:template>
    
    <xsl:template match="res:status" mode="test-status-label">
        <xsl:variable name="current-req" select="../../@id"/>
        <xsl:variable name="key" select="$requirements/key('requirement-index', $current-req)"/>
        <xsl:choose>
            <xsl:when test="text() = 'PASS'"><span class="label label-success">Pass</span></xsl:when>
            <xsl:when test="text() = 'FAIL'">
                <xsl:choose>
                    <xsl:when test="@severity = 'ERROR'"><span class="label label-danger">Error</span></xsl:when>
                    <xsl:when test="@severity = 'WARNING'"><span class="label label-warning">Warning</span></xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="text() = 'NOT_TESTED'"><span class="label label-default">Not Tested</span></xsl:when>
            <xsl:when test="text() = 'NOT_APPLICABLE'"><span class="label label-default">Not Applicable</span></xsl:when>
            <xsl:when test="text() = 'INFORMATIONAL'"><span class="label label-info">Informational</span></xsl:when>
            <xsl:otherwise><xsl:value-of select="text()"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>