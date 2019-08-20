package no.difi.vefa.validator.checker;

import no.difi.vefa.validator.lang.ValidatorException;
import org.testng.annotations.Test;

public class SchematronXsltCheckerTest {

    @Test(expectedExceptions = ValidatorException.class)
    public void simpleTriggerException() throws Exception {
        new SchematronXsltCheckerFactory().prepare(null, null);
    }
}
