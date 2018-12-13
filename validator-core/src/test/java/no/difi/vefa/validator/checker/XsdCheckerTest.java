package no.difi.vefa.validator.checker;

import no.difi.vefa.validator.api.ValidatorException;
import org.testng.annotations.Test;

public class XsdCheckerTest {

    @Test(expectedExceptions = ValidatorException.class)
    public void simpleTriggerException() throws Exception {
        new XsdCheckerFactory().prepare(null);
    }
}
