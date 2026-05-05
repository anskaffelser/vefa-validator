package no.dfo.anskaffelser.vefa.validator.source;

import no.dfo.anskaffelser.vefa.validator.api.Properties;
import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

public class DirectorySourceTest {

    @Test(expectedExceptions = ValidatorException.class)
    public void triggerException() throws ValidatorException {
        DirectorySource source = new DirectorySource(null);
        source.createInstance(Mockito.mock(Properties.class));
    }
}
