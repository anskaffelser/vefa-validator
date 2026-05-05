package no.dfo.anskaffelser.vefa.validator.checker;

import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;
import org.testng.annotations.Test;

public class XsdCheckerTest {

    @Test(expectedExceptions = ValidatorException.class)
    public void simpleTriggerException() throws Exception {
        new XsdCheckerFactory().prepare(null, null);
    }
}
