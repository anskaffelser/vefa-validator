<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns="http://difi.no/xsd/vefa/validator/1.0/internal"
                exclude-result-prefixes="xs"
                version="2.0">

    <xsl:output indent="yes"/>

    <!-- SBDH -->
    <xsl:template match="/sbdh:StandardBusinessDocumentHeader" priority="1000"
                  xmlns:sbdh="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"
                  exclude-result-prefixes="sbdh">
        <Identification type="xml.sbdh">
            <Id>
                <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
            </Id>
            <Id>SBDH:1.0</Id>
            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <Property key="xml.namespace">
                <xsl:value-of select="namespace-uri()"/>
            </Property>
        </Identification>
    </xsl:template>

    <!-- SBD -->
    <xsl:template match="/sbdh:StandardBusinessDocument" priority="1000"
                  xmlns:sbdh="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"
                  exclude-result-prefixes="sbdh">
        <Identification type="xml.sbdh">
            <Id>
                <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
            </Id>
            <Id>SBDH:1.0</Id>
            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <Property key="xml.namespace">
                <xsl:value-of select="namespace-uri()"/>
            </Property>
            <xsl:if test="*[2]">
                <Children>
                    <xsl:copy-of select="*[2]"/>
                </Children>
            </xsl:if>
        </Identification>
    </xsl:template>

    <!-- UBL -->
    <xsl:template
            match="/*[starts-with(namespace-uri(), 'urn:oasis:names:specification:ubl:schema:xsd:') and ends-with(namespace-uri(), '-2')]"
            xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
            exclude-result-prefixes="cbc"
            priority="1000">
        <Identification type="xml.ubl">
            <xsl:if test="cbc:CustomizationID[1] and cbc:ProfileID[1]">
                <Id>
                    <xsl:value-of
                            select="concat(cbc:ProfileID[1]/normalize-space(), '#', cbc:CustomizationID[1]/normalize-space())"/>
                </Id>
            </xsl:if>
            <xsl:if test="cbc:CustomizationID[1]">
                <Id>
                    <xsl:value-of select="cbc:CustomizationID[1]/normalize-space()"/>
                </Id>
            </xsl:if>
            <xsl:if test="cbc:CustomizationID[1] and cbc:ProfileID[1]">
                <Id>
                    <xsl:value-of
                            select="concat(local-name(), '::', cbc:ProfileID[1]/normalize-space(), '#', cbc:CustomizationID[1]/normalize-space())"/>
                </Id>
            </xsl:if>
            <xsl:if test="cbc:CustomizationID[1]">
                <Id>
                    <xsl:value-of select="concat(local-name(), '::', cbc:CustomizationID[1]/normalize-space())"/>
                </Id>
            </xsl:if>
            <Id>
                <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
            </Id>
            <Id>
                <xsl:value-of select="local-name()"/>
            </Id>

            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <Property key="xml.namespace">
                <xsl:value-of select="namespace-uri()"/>
            </Property>
            <xsl:if test="cbc:CustomizationID[1]">
                <Property key="ubl.CustomizationID">
                    <xsl:value-of select="normalize-space(cbc:CustomizationID[1])"/>
                </Property>
            </xsl:if>
            <xsl:if test="cbc:ProfileID[1]">
                <Property key="ubl.ProfileID">
                    <xsl:value-of select="normalize-space(cbc:ProfileID[1])"/>
                </Property>
            </xsl:if>
            <xsl:if test="cbc:VersionID[1]">
                <Property key="ubl.VersionID">
                    <xsl:value-of select="normalize-space(cbc:VersionID[1])"/>
                </Property>
            </xsl:if>
        </Identification>
    </xsl:template>

    <!-- UN Cefact -->
    <xsl:template match="/*[starts-with(namespace-uri(), 'urn:un:unece:uncefact:data:standard:')]" priority="1000"
                  xmlns:ram="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100"
                  exclude-result-prefixes="ram">
        <xsl:variable name="process"
                      select="*:ExchangedDocumentContext[1]/ram:BusinessProcessSpecifiedDocumentContextParameter[1]/ram:ID[1]/normalize-space()"/>
        <xsl:variable name="guideline"
                      select="*:ExchangedDocumentContext[1]/ram:GuidelineSpecifiedDocumentContextParameter[1]/ram:ID[1]/normalize-space()"/>

        <Identification type="xml.uncefact">
            <xsl:if test="namespace-uri()">
                <xsl:if test="$process and $guideline">
                    <Id>
                        <xsl:value-of select="concat(local-name(), '::', $process, '::', $guideline)"/>
                    </Id>
                </xsl:if>
                <xsl:if test="$guideline">
                    <Id>
                        <xsl:value-of select="concat(local-name(), '::', $guideline)"/>
                    </Id>
                </xsl:if>
                <xsl:if test="$process">
                    <Id>
                        <xsl:value-of select="concat(local-name(), '::', $process)"/>
                    </Id>
                </xsl:if>
                <Id>
                    <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
                </Id>
            </xsl:if>
            <Id>
                <xsl:value-of select="local-name()"/>
            </Id>
            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <Property key="xml.namespace">
                <xsl:value-of select="namespace-uri()"/>
            </Property>
            <xsl:if test="$process">
                <Property key="uncefact.BusinessProcess">
                    <xsl:value-of select="$process"/>
                </Property>
            </xsl:if>
            <xsl:if test="$guideline">
                <Property key="uncefact.Guideline">
                    <xsl:value-of select="$guideline"/>
                </Property>
            </xsl:if>
        </Identification>
    </xsl:template>

    <!-- Validator Test -->
    <xsl:template match="/val:test" xmlns:val="http://difi.no/xsd/vefa/validator/1.0" priority="1000">
        <Identification type="xml.test">
            <Id>
                <xsl:value-of select="concat('configuration::', @configuration)"/>
            </Id>

            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <Property key="xml.namespace">
                <xsl:value-of select="namespace-uri()"/>
            </Property>
        </Identification>
    </xsl:template>

    <!-- Validator TestSet -->
    <xsl:template match="/val:testSet" xmlns:val="http://difi.no/xsd/vefa/validator/1.0" priority="1000">
        <Identification type="xml.testset">
            <Id>
                <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
            </Id>
            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <Property key="xml.namespace">
                <xsl:value-of select="namespace-uri()"/>
            </Property>
            <Children>
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
            </Children>
        </Identification>
    </xsl:template>

    <!-- ESPD -->
    <xsl:template match="espdrequest:ESPDRequest | espdresponse:ESPDResponse" priority="1000"
                  xmlns:espdrequest="urn:grow:names:specification:ubl:schema:xsd:ESPDRequest-1"
                  xmlns:espdresponse="urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1"
                  xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
                  exclude-result-prefixes="espdrequest espdresponse cbc">
        <Identification type="xml.espd">
            <xsl:if test="cbc:CustomizationID[1]">
                <Id>
                    <xsl:value-of
                            select="concat(namespace-uri(), '::', local-name(), '::', normalize-space(cbc:CustomizationID[1]))"/>
                </Id>
                <Id>
                    <xsl:value-of select="concat(local-name(), '::', normalize-space(cbc:CustomizationID[1]))"/>
                </Id>
            </xsl:if>
            <xsl:if test="cbc:VersionID[1]">
                <Id>
                    <xsl:value-of
                            select="concat(namespace-uri(), '::', local-name(), '::', normalize-space(cbc:VersionID[1]))"/>
                </Id>
                <Id>
                    <xsl:value-of select="concat(local-name(), '::', normalize-space(cbc:VersionID[1]))"/>
                </Id>
            </xsl:if>
            <Id>
                <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
            </Id>
            <Id>
                <xsl:value-of select="local-name()"/>
            </Id>
            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <Property key="xml.namespace">
                <xsl:value-of select="namespace-uri()"/>
            </Property>
            <xsl:if test="cbc:CustomizationID[1]">
                <Property key="ubl.CustomizationID">
                    <xsl:value-of select="normalize-space(cbc:CustomizationID[1])"/>
                </Property>
            </xsl:if>
            <xsl:if test="cbc:ProfileID[1]">
                <Property key="ubl.ProfileID">
                    <xsl:value-of select="normalize-space(cbc:ProfileID[1])"/>
                </Property>
            </xsl:if>
            <xsl:if test="cbc:VersionID[1]">
                <Property key="ubl.VersionID">
                    <xsl:value-of select="normalize-space(cbc:VersionID[1])"/>
                </Property>
            </xsl:if>
        </Identification>
    </xsl:template>

    <!-- Other -->
    <xsl:template match="/*" priority="0">
        <Identification type="xml">
            <xsl:if test="namespace-uri()">
                <Id>
                    <xsl:value-of select="concat(namespace-uri(), '::', local-name())"/>
                </Id>
            </xsl:if>
            <Id>
                <xsl:value-of select="local-name()"/>
            </Id>
            <Property key="xml.element">
                <xsl:value-of select="local-name()"/>
            </Property>
            <xsl:if test="namespace-uri()">
                <Property key="xml.namespace">
                    <xsl:value-of select="namespace-uri()"/>
                </Property>
            </xsl:if>
        </Identification>
    </xsl:template>

</xsl:stylesheet>