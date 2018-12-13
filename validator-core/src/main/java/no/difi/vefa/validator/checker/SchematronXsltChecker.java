package no.difi.vefa.validator.checker;

import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import no.difi.commons.schematron.jaxb.svrl.FailedAssert;
import no.difi.commons.schematron.jaxb.svrl.NsPrefixInAttributeValues;
import no.difi.commons.schematron.jaxb.svrl.SchematronOutput;
import no.difi.commons.schematron.jaxb.svrl.SuccessfulReport;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.vefa.validator.util.SaxonErrorListener;
import no.difi.vefa.validator.util.SaxonHelper;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
@CheckerInfo({".xsl", ".xslt", ".svrl.xsl", ".svrl.xslt"})
public class SchematronXsltChecker implements Checker {

    private static JAXBContext jaxbContext = JAXBHelper.context(SchematronOutput.class);

    private XsltExecutable xsltExecutable;

    public SchematronXsltChecker(XsltExecutable xsltExecutable) {
        this.xsltExecutable = xsltExecutable;
    }

    @Override
    public void check(Document document, Section section) throws ValidatorException {
        long tsStart = System.currentTimeMillis();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            XsltTransformer xsltTransformer = xsltExecutable.load();
            xsltTransformer.setErrorListener(SaxonErrorListener.INSTANCE);
            xsltTransformer.setSource(new StreamSource(document.getInputStream()));
            xsltTransformer.setDestination(SaxonHelper.PROCESSOR.newSerializer(baos));
            xsltTransformer.transform();
            xsltTransformer.close();

            long tsEnd = System.currentTimeMillis();

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SchematronOutput output = unmarshaller.unmarshal(new StreamSource(
                    new ByteArrayInputStream(baos.toByteArray())), SchematronOutput.class).getValue();

            section.setTitle(output.getTitle());
            section.setRuntime((tsEnd - tsStart) + "ms");

            for (Object o : output.getActivePatternAndFiredRuleAndFailedAssert())
                if (o instanceof FailedAssert)
                    add(section, (FailedAssert) o, output.getNsPrefixInAttributeValues());
                else if (o instanceof SuccessfulReport)
                    add(section, (SuccessfulReport) o, output.getNsPrefixInAttributeValues());
        } catch (Exception e) {
            throw new ValidatorException(
                    String.format("Unable to perform check: %s", e.getMessage()), e);
        }
    }

    public void add(Section section, FailedAssert failedAssert, List<NsPrefixInAttributeValues> namespaces) {
        AssertionType assertionType = new AssertionType();

        String text = failedAssert.getText().replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").replaceAll("     ", " ").replaceAll("   ", " ").replaceAll("  ", " ");
        if (text.startsWith("[") && text.contains("]-")) {
            assertionType.setIdentifier(text.substring(1, text.indexOf("]-")).trim());
            text = text.substring(text.indexOf("]-") + 2).trim();
        } else {
            assertionType.setIdentifier("UNKNOWN");
        }

        if (failedAssert.getId() != null)
            assertionType.setIdentifier(failedAssert.getId());

        assertionType.setText(text);
        assertionType.setLocation(failedAssert.getLocation());
        assertionType.setLocationFriendly(getFriendlyLocation(failedAssert.getLocation(), namespaces));
        assertionType.setTest(failedAssert.getTest());

        if (failedAssert.getFlag() == null) {
            assertionType.setFlag(FlagType.ERROR);
        } else {
            switch (failedAssert.getFlag()) {
                case "fatal":
                    assertionType.setFlag(FlagType.ERROR);
                    break;
                case "warning":
                    assertionType.setFlag(FlagType.WARNING);
                    break;
                default:
                    log.warn("Unknown: " + failedAssert.getFlag());
                    break;
            }
        }

        section.add(assertionType);
    }

    public void add(Section section, SuccessfulReport failedAssert, List<NsPrefixInAttributeValues> namespaces) {
        AssertionType assertionType = new AssertionType();

        String text = failedAssert.getText().replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").replaceAll("     ", " ").replaceAll("   ", " ").replaceAll("  ", " ");
        if (text.startsWith("[") && text.contains("]-")) {
            assertionType.setIdentifier(text.substring(1, text.indexOf("]-")).trim());
            text = text.substring(text.indexOf("]-") + 2).trim();
        } else {
            assertionType.setIdentifier("UNKNOWN");
        }

        if (failedAssert.getId() != null)
            assertionType.setIdentifier(failedAssert.getId());

        assertionType.setText(text);
        assertionType.setLocation(failedAssert.getLocation());
        assertionType.setLocationFriendly(getFriendlyLocation(failedAssert.getLocation(), namespaces));
        assertionType.setTest(failedAssert.getTest());

        switch (failedAssert.getFlag()) {
            case "info":
                assertionType.setFlag(FlagType.INFO);
                break;
            default:
                assertionType.setFlag(FlagType.SUCCESS);
                break;
        }

        section.add(assertionType);
    }

    private String getFriendlyLocation(String location, List<NsPrefixInAttributeValues> namespaces) {
        String locationFriendly = location;
        for (NsPrefixInAttributeValues ns : namespaces) {
            locationFriendly = locationFriendly.replaceAll(
                    ":([\\p{Alnum}]+?)\\[namespace-uri\\(\\)='" + Matcher.quoteReplacement(ns.getUri()) + "'\\]",
                    ns.getPrefix() + ":$1"
            );
        }

        return locationFriendly.replace("/*", " \\ ").trim();
    }
}
