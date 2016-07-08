package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.TreeSet;

public class DirectorySourceTest {

    @Test(expectedExceptions = ValidatorException.class)
    public void triggerException() throws ValidatorException{
        DirectorySource source = new DirectorySource(null);
        source.createInstance(Mockito.mock(Properties.class));
    }
}
