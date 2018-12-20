<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:sbdh="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"
                exclude-result-prefixes="xs xsl sbdh">

    <xsl:template match="sbdh:StandardBusinessDocument">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="sbdh:StandardBusinessDocumentHeader"/>

    <xsl:template match="node()">
        <xsl:copy-of select="current()"/>
    </xsl:template>

</xsl:stylesheet>