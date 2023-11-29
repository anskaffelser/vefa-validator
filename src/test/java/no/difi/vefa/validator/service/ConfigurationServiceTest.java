package no.difi.vefa.validator.service;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.util.Repositories;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ConfigurationServiceTest {

    @Inject
    private ConfigurationService configurationService;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule(Repositories.classpath("/rules/"))).injectMembers(this);
    }

    @Test
    public void simple() {
        Assert.assertNotNull(configurationService);
        Assert.assertEquals(configurationService.getPackages().size(), 6);
    }
}
