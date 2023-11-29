<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:val="http://difi.no/xsd/vefa/validator/1.0"
                xmlns="http://difi.no/xsd/vefa/validator/1.0"
                exclude-result-prefixes="xs val"
                version="2.0">

    <!-- Validator Test -->
    <xsl:template match="/val:test" priority="1000">
        <xsl:copy-of select="current()"/>
    </xsl:template>

    <!-- Validator TestSet -->
    <xsl:template match="/val:testSet" xmlns:val="http://difi.no/xsd/vefa/validator/1.0" priority="1000">
        <testSet>
            <xsl:variable name="assert" select="val:assert"/>
            <xsl:variable name="configuration" select="@configuration"/>

            <xsl:for-each select="val:test">
                <test xmlns="http://difi.no/xsd/vefa/validator/1.0">
                    <xsl:attribute name="configuration"
                                   select="if (@configuration) then @configuration else $configuration"/>
                    <xsl:attribute name="id" select="if (@id) then @id else position()"/>

                    <assert>
                        <xsl:copy-of
                                select="val:assert/val:scope | val:assert/val:fatal | val:assert/val:error | val:assert/val:warning | val:assert/val:success"/>
                        <xsl:copy-of
                                select="$assert/val:scope | $assert/val:fatal | $assert/val:error | $assert/val:warning | $assert/val:success"/>
                        <xsl:copy-of
                                select="if (val:assert/val:description) then val:assert/val:description else $assert/val:description"/>
                    </assert>
                    <xsl:copy-of select="if (val:assert) then *[2] else *[1]"/>
                </test>
            </xsl:for-each>
        </testSet>
    </xsl:template>

</xsl:stylesheet>