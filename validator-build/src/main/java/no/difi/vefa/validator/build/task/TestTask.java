package no.difi.vefa.validator.build.task;

import com.google.inject.Singleton;
import no.difi.vefa.validator.build.model.Build;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.tester.Tester;

/**
 * @author erlend
 */
@Singleton
public class TestTask {

    public void perform(Build build) {
        for (Validation validation : Tester.perform(build.getTargetFolder(), build.getTestFolders()))
            build.addTestValidation(validation);
    }
}
