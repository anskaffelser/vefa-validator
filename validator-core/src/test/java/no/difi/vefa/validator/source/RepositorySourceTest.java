package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.TreeSet;

public class RepositorySourceTest {

    // Dump test
    @Test
    public void simple() {
        Assert.assertNotNull(RepositorySource.forTest());
        Assert.assertNotNull(RepositorySource.forProduction());
    }

    @Test(expectedExceptions = ValidatorException.class)
    public void triggerException() throws ValidatorException{
        RepositorySource source = new RepositorySource((URI) null);
        source.createInstance(Mockito.mock(Properties.class));
    }
}
