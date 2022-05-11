<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
                xmlns="http://www.w3.org/2005/xpath-functions"
                version="2.0">

    <xsl:output method="text"/>

    <xsl:template match="*">
        <xsl:variable name="content">
            <array>
                <xsl:if test="cbc:CustomizationID[1] and cbc:ProfileID[1]">
                    <string>
                        <xsl:value-of
                                select="concat(cbc:ProfileID[1]/normalize-space(), '#', cbc:CustomizationID[1]/normalize-space())"/>
                    </string>
                </xsl:if>
                <xsl:if test="cbc:CustomizationID[1]">
                    <string>
                        <xsl:value-of select="cbc:CustomizationID[1]/normalize-space()"/>
                    </string>
                </xsl:if>
                <xsl:if test="cbc:CustomizationID[1] and cbc:ProfileID[1]">
                    <string>
                        <xsl:value-of
                                select="concat(local-name(), '::', cbc:ProfileID[1]/normalize-space(), '#', cbc:CustomizationID[1]/normalize-space())"/>
                    </string>
                </xsl:if>
                <xsl:if test="cbc:CustomizationID[1]">
                    <string>
                        <xsl:value-of select="concat(local-name(), '::', cbc:CustomizationID[1]/normalize-space())"/>
                    </string>
                </xsl:if>
                <string>
                    <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
                </string>
                <string>
                    <xsl:value-of select="local-name()"/>
                </string>
            </array>
        </xsl:variable>

        <xsl:value-of select="xml-to-json($content)"/>
    </xsl:template>

</xsl:stylesheet>