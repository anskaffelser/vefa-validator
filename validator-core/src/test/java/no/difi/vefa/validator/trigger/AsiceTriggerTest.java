package no.difi.vefa.validator.trigger;

import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.plugin.AsicePlugin;
import no.difi.vefa.validator.source.ClasspathSource;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AsiceTriggerTest {

    private Validator validator;

    @BeforeClass
    public void beforeClass() throws ValidatorException {
        validator = ValidatorBuilder.emptyValidator()
                .setSource(new ClasspathSource("/rules/"))
                .plugin(AsicePlugin.class)
                .build();
    }

    @AfterClass
    public void afterClass() {
        validator.close();
    }

    @Test(enabled = false)
    public void simpleInvalidAsice() throws ValidatorException {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/asic-cades-test-invalid-signature.asice"));
        Assert.assertEquals(validation.getReport().getFlag(), FlagType.FATAL);
    }

    @Test
    public void simpleValidAsice() throws ValidatorException {
        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/asic-cades-test-valid.asice"));
        Assert.assertEquals(validation.getReport().getFlag(), FlagType.OK);
    }
}