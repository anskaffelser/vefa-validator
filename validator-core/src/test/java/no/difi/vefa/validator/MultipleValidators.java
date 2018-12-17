package no.difi.vefa.validator;

import no.difi.vefa.validator.source.ClasspathSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing opening and closing two validators in row.
 */
public class MultipleValidators {

    private Validator validator;

    @Before
    public void setUp() {
        validator = ValidatorBuilder.newValidator()
                .setSource(new ClasspathSource("/rules/"))
                .build();
    }

    @After
    public void tearDown() {
        validator.close();
    }

    @Test
    public void test1() {
        // No action
    }

    @Test
    public void test2() {
        // No action
    }

}
