package no.difi.vefa.validator.builder;

import no.difi.xsd.vefa.validator._1.ConfigurationType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigurationBuilderTest {

    @Test
    public void simple() {
        ConfigurationType cfg = ConfigurationBuilder
                .identifier("test")
                .title("Test")
                .standardId("test#test")
                .weight(-10L)
                .trigger("test")
                .build("unit-test")
                .build();

        Assert.assertEquals(cfg.getIdentifier(), "test");
        Assert.assertEquals(cfg.getTitle(), "Test");
        Assert.assertEquals(cfg.getStandardId(), "test#test");
        Assert.assertEquals(cfg.getWeight(), -10L);
        Assert.assertEquals(cfg.getTrigger().size(), 1);
        Assert.assertEquals(cfg.getTrigger().get(0).getIdentifier(), "test");
        Assert.assertEquals(cfg.getBuild(), "unit-test");
    }

}
