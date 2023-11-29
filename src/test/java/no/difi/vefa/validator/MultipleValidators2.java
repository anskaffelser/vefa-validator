package no.difi.vefa.validator;

import no.difi.vefa.validator.util.Repositories;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Using multiple validators at the same time.
 */
public class MultipleValidators2 {

    @Test
    public void simple() {
        Validator validator1 = ValidatorBuilder.newValidator()
                .setRepository(Repositories.classpath("/rules/"))
                .build();
        Validator validator2 = ValidatorBuilder.newValidator()
                .setRepository(Repositories.classpath("/rules/"))
                .build();

        Assert.assertNotSame(validator1, validator2);

        validator1.close();
        validator2.close();
    }
}
