package no.dfo.anskaffelser.vefa.validator.trigger;

import no.dfo.anskaffelser.vefa.validator.Validator;
import no.dfo.anskaffelser.vefa.validator.ValidatorBuilder;
import no.dfo.anskaffelser.vefa.validator.api.Validation;
import no.dfo.anskaffelser.vefa.validator.source.ClasspathSource;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

public class AsiceTriggerTest {

    private Validator validator;

    @BeforeClass
    public void beforeClass() {
        validator = ValidatorBuilder.newValidator()
                .setSource(new ClasspathSource("/rules/"))
                .build();
    }

    @AfterClass
    public void afterClass() {
        validator.close();
    }

    @Test(enabled = false)
    public void simpleInvalidAsice() {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/asic-cades-test-invalid-signature.asice"));
        Assert.assertEquals(validation.getReport().getFlag(), FlagType.FATAL);
    }

    @Test
    public void simpleValidAsice() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/documents/asic-cades-test-valid.asice")) {
            Validation validation = validator.validate(inputStream);
            Assert.assertEquals(validation.getReport().getFlag(), FlagType.OK);
        }
    }
}