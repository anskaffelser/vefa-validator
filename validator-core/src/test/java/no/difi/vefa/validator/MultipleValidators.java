package no.difi.vefa.validator;

import no.difi.vefa.validator.source.ClasspathSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

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
        Assert.assertNotNull(validator);
    }

    @Test
    public void test2() {
        Assert.assertNotNull(validator);
    }

}
