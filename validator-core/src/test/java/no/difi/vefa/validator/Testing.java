package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.ClasspathSource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

import static org.testng.Assert.assertEquals;

public class Testing {

    private static Logger logger = LoggerFactory.getLogger(Testing.class);

    private static Validator validator;

    @BeforeClass
    public void beforeClass() throws Exception {
        validator = ValidatorBuilder
                .newValidator()
                .setProperties(new SimpleProperties()
                        .set("feature.expectation", true))
                .setSource(new ClasspathSource("/rules/"))
                .build();
    }

    @AfterClass
    public void afterClass() throws Exception {
        validator.close();
        validator = null;
    }

    @Test
    public void simpleError() throws Exception {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/T10-hode-feilkoder.xml"));

        for (SectionType sectionType : validation.getReport().getSection()) {
            logger.info(sectionType.getTitle() + ": " + sectionType.getRuntime());
            for (AssertionType assertion : sectionType.getAssertion())
                logger.info(String.format("- [%s] %s (%s)", assertion.getIdentifier(), assertion.getText(), assertion.getFlag()));
        }

        OutputStream outputStream = new FileOutputStream("target/test-simple-feilkoder.html");
        validation.render(outputStream);
        outputStream.close();

        assertEquals(validation.getReport().getFlag(), FlagType.ERROR);
        assertEquals(validation.getReport().getSection().get(5).getAssertion().size(), 5);
        assertEquals(validation.getDocument().getDeclaration(), "xml.ubl::urn:www.cenbii.eu:profile:bii04:ver2.0#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0");
    }

    @Test
    public void simpleOk() throws Exception {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/ehf-invoice-2.0.xml"));

        for (SectionType sectionType : validation.getReport().getSection()) {
            logger.info(sectionType.getTitle() + ": " + sectionType.getRuntime());
            for (AssertionType assertion : sectionType.getAssertion())
                logger.info(String.format("- [%s] %s (%s)", assertion.getIdentifier(), assertion.getText(), assertion.getFlag()));
        }

        OutputStream outputStream = new FileOutputStream("target/test-simple-invoice.html");
        validation.render(outputStream);
        outputStream.close();

        assertEquals(validation.getReport().getFlag(), FlagType.OK);
        assertEquals(validation.getDocument().getDeclaration(), "xml.ubl::urn:www.cenbii.eu:profile:bii05:ver2.0#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0");
    }

    @Test
    public void simpleValidatorTest() {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/NOGOV-T10-R014.xml"), new SimpleProperties().set("feature.nesting", true));
        Assert.assertEquals(validation.getReport().getFlag(), FlagType.OK);
        Assert.assertEquals(validation.getChildren().size(), 3);
    }
}
