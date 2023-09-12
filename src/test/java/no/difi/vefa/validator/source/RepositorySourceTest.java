package no.difi.vefa.validator.source;

import no.difi.vefa.validator.lang.ValidatorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

public class RepositorySourceTest {

    // Dump test
    @Test
    public void simple() {
        Assert.assertNotNull(RepositorySource.forTest());
        Assert.assertNotNull(RepositorySource.forProduction());
    }

    @Test(expectedExceptions = ValidatorException.class)
    public void triggerException() throws ValidatorException {
        RepositorySource source = new RepositorySource((URI) null);
        source.createInstance();
    }
}
