package no.difi.vefa.validator.source;

import no.difi.vefa.validator.lang.ValidatorException;
import org.testng.annotations.Test;

public class DirectorySourceTest {

    @Test(expectedExceptions = ValidatorException.class)
    public void triggerException() throws ValidatorException {
        DirectorySource source = new DirectorySource(null);
        source.createInstance();
    }
}
