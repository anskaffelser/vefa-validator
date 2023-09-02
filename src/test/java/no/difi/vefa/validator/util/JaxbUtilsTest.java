package no.difi.vefa.validator.util;

import no.difi.xsd.vefa.validator._1.Configurations;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JaxbUtilsTest {

    @Test
    public void simpleSuccess() {
        Assert.assertNotNull(JaxbUtils.context(Configurations.class));
    }

    @Test(expectedExceptions = RuntimeException.class)
    @SuppressWarnings("all")
    public void simpleError() {
        JaxbUtils.context(null);
    }
}
