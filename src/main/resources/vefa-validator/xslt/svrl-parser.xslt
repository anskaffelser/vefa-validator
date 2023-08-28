<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:svrl="http://purl.oclc.org/dsdl/svrl"
                xmlns="http://difi.no/xsd/vefa/validator/1.0"
                exclude-result-prefixes="xs xsl svrl">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/svrl:schematron-output">
        <section>
            <xsl:attribute name="title" select="@title"/>

            <xsl:apply-templates select="svrl:failed-assert | svrl:successful-report"/>
        </section>
    </xsl:template>

    <xsl:template match="svrl:failed-assert">
        <assertion>
            <xsl:variable name="text" select="svrl:text/text()"/>
            <xsl:variable name="identifier" select="if (@id) then @id else (if (matches($text, '^\[(.+?)\]\-')) then replace($text, '^\[(.+?)\]\-(.*)', '$1') else 'UNKNOWN')"/>

            <xsl:attribute name="identifier" select="$identifier"/>
            <xsl:attribute name="flag" select="if (@flag) then svrl:assert-flag(@flag) else 'error'"/>

            <text><xsl:value-of select="if (starts-with($text, concat('[', $identifier, ']'))) then substring($text, string-length($identifier) + 4) else $text"/></text>
            <location><xsl:value-of select="@location"/></location>
            <locationFriendly><xsl:value-of select="svrl:friendly(@location, //svrl:ns-prefix-in-attribute-values, 1)"/></locationFriendly>
            <test><xsl:value-of select="@test"/></test>
        </assertion>
    </xsl:template>

    <xsl:template match="svrl:successful-report">
        <assertion>
            <xsl:variable name="text" select="svrl:text/text()"/>
            <xsl:variable name="identifier" select="if (@id) then @id else (if (matches($text, '^\[(.+?)\]\-')) then replace($text, '^\[(.+?)\]\-(.*)', '$1') else 'UNKNOWN')"/>

            <xsl:attribute name="identifier" select="$identifier"/>
            <xsl:attribute name="flag" select="if (@flag) then svrl:success-flag(@flag) else 'success'"/>

            <text><xsl:value-of select="if (starts-with($text, concat('[', $identifier, ']'))) then substring($text, string-length($identifier) + 4) else $text"/></text>
            <location><xsl:value-of select="@location"/></location>
            <locationFriendly><xsl:value-of select="svrl:friendly(@location, //svrl:ns-prefix-in-attribute-values, 1)"/></locationFriendly>
            <test><xsl:value-of select="@test"/></test>
        </assertion>
    </xsl:template>

    <xsl:function name="svrl:friendly" as="xs:string">
        <xsl:param name="location" as="xs:string"/>
        <xsl:param name="ns" as="item()*"/>
        <xsl:param name="count" as="xs:integer"/>

        <xsl:choose>
            <xsl:when test="$count > count($ns)">
                <xsl:value-of select="replace($location, '\[namespace\-uri\(\)=''(.+?)''\]', '')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="prefix" select="$ns[$count]/@prefix"/>
                <xsl:variable name="uri" select="$ns[$count]/@uri"/>

                <xsl:value-of select="svrl:friendly(replace($location, concat('\*:([\w]+?)\[namespace\-uri\(\)=''', $uri, '''\]'), concat($prefix, ':$1')), $ns, $count + 1)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="svrl:assert-flag" as="xs:string">
        <xsl:param name="source" as="xs:string"/>

        <xsl:choose>
            <xsl:when test="$source = 'fatal'">
                <xsl:value-of select="'error'"/>
            </xsl:when>
            <xsl:when test="$source = 'warning'">
                <xsl:value-of select="'warning'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'unknown'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="svrl:success-flag" as="xs:string">
        <xsl:param name="source" as="xs:string"/>

        <xsl:choose>
            <xsl:when test="$source = 'info'">
                <xsl:value-of select="'info'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'success'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

</xsl:stylesheet>