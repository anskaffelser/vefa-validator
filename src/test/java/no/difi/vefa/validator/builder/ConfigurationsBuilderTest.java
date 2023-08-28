package no.difi.vefa.validator.builder;

import no.difi.xsd.vefa.validator._1.Configurations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigurationsBuilderTest {

    @Test
    public void simple() {
        Configurations cfg = ConfigurationsBuilder
                .instance()
                .configuration(ConfigurationBuilder.identifier("test").build())
                .pkg("Test")
                .build();

        Assert.assertEquals(cfg.getConfiguration().size(), 1);
        Assert.assertNull(cfg.getPackage().get(0).getUrl());
        Assert.assertEquals(cfg.getPackage().get(0).getValue(), "Test");
    }
}
