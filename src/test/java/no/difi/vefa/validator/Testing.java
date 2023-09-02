package no.difi.vefa.validator;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.ClasspathSource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Slf4j
public class Testing {

    private static Validator validator;

    @BeforeClass
    public void beforeClass() {
        validator = ValidatorBuilder
                .newValidator()
                .setProperties(new SimpleProperties()
                        .set("feature.expectation", true))
                .setSource(new ClasspathSource("/rules/"))
                .build();
    }

    @AfterClass
    public void afterClass() {
        validator.close();
        validator = null;
    }

    @Test
    public void simpleError() {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/T10-hode-feilkoder.xml"));

        for (SectionType sectionType : validation.getReport().getSection()) {
            log.info(sectionType.getTitle() + ": " + sectionType.getRuntime());
            for (AssertionType assertion : sectionType.getAssertion())
                log.info(String.format(
                        "- [%s] %s (%s)", assertion.getIdentifier(), assertion.getText(), assertion.getFlag()));
        }

        assertEquals(validation.getReport().getFlag(), FlagType.ERROR);
        assertEquals(validation.getReport().getSection().get(5).getAssertion().size(), 5);
        assertEquals(validation.getDocument().getDeclarations().get(0),
                "xml.ubl::urn:www.cenbii.eu:profile:bii04:ver2.0#" +
                        "urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:" +
                        "urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:" +
                        "urn:www.difi.no:ehf:faktura:ver2.0");
    }

    @Test
    public void simpleOk() {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/ehf-invoice-2.0.xml"));

        for (SectionType sectionType : validation.getReport().getSection()) {
            log.info(sectionType.getTitle() + ": " + sectionType.getRuntime());
            for (AssertionType assertion : sectionType.getAssertion())
                log.info(String.format(
                        "- [%s] %s (%s)", assertion.getIdentifier(), assertion.getText(), assertion.getFlag()));
        }

        assertEquals(validation.getReport().getFlag(), FlagType.OK);
        assertEquals(validation.getDocument().getDeclarations().get(0), "xml.ubl::urn:www.cenbii.eu:profile:bii05:ver2.0#" +
                "urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:" +
                "urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:" +
                "urn:www.difi.no:ehf:faktura:ver2.0");
    }

    @Test
    public void simpleValidatorTest() {
        var  validation = validator.validate(
                getClass().getResourceAsStream("/documents/NOGOV-T10-R014.xml"),
                new SimpleProperties().set("feature.nesting", true));

        assertEquals(validation.getReport().getFlag(), FlagType.OK);
        assertEquals(validation.getChildren().size(), 3);
    }

    @Test
    public void billing3Test() {
        var validation = validator.validate(
                getClass().getResourceAsStream("/documents/peppol-billing-3.0.xml"));

        assertEquals(validation.getReport().getFlag(), FlagType.OK);
        assertEquals(validation.getReport().getTitle(), "PEPPOL BIS Billing 3.0 (Profile 01)");
    }

    @Test
    public void testValidationWithLongUblExtension() {
        var validation = validator.validate(
                getClass().getResourceAsStream("/documents/peppol-billing-3.0_long_ubl_extension.xml"));

        assertEquals(validation.getReport().getFlag(), FlagType.WARNING);
        assertEquals(validation.getReport().getTitle(), "PEPPOL BIS Billing 3.0 (Profile 01)");
    }

    @Test
    public void testValidationEmptyUbl() {
        var validation = validator.validate(
                getClass().getResourceAsStream("/documents/ubl-invoice-empty.xml"));

        assertEquals(validation.getReport().getFlag(), FlagType.UNKNOWN);
    }
}
