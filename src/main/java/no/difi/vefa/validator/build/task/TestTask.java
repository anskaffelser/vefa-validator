package no.difi.vefa.validator.build.task;

import com.google.inject.Singleton;
import no.difi.vefa.validator.build.model.Build;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.tester.Tester;
import no.difi.xsd.vefa.validator._1.FlagType;

import java.io.IOException;

/**
 * @author erlend
 */
@Singleton
public class TestTask {

    public boolean perform(Build build) throws IOException {
        for (Validation validation : Tester.perform(build.getTargetFolder(), build.getTestFolders()))
            build.addTestValidation(validation);

        for (Validation validation : build.getTestValidations())
            if (validation.getReport().getFlag().compareTo(FlagType.EXPECTED) > 0)
                return false;

        return true;
    }
}
