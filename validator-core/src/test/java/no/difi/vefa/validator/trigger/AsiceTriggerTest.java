package no.difi.vefa.validator.trigger;

import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.source.ClasspathSource;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
    public void simpleValidAsice() {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/asic-cades-test-valid.asice"));
        Assert.assertEquals(validation.getReport().getFlag(), FlagType.OK);
    }
}