package no.difi.vefa.validator;

import no.difi.vefa.validator.util.Repositories;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Testing opening and closing two validators in row.
 */
public class MultipleValidators {

    private Validator validator;

    @BeforeClass
    public void setUp() {
        validator = ValidatorBuilder.newValidator()
                .setRepository(Repositories.classpath("/rules/"))
                .build();
    }

    @AfterClass
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
