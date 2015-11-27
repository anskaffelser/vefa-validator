package no.difi.vefa.validator.build.task;

import no.difi.vefa.validator.Validation;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.build.api.Build;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.DirectorySource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class TestTask {
    private static Logger logger = LoggerFactory.getLogger(TestTask.class);

    public void test(Build build) throws Exception {
        Validator validator = ValidatorBuilder
                .newValidator()
                .setProperties(new SimpleProperties()
                                .set("feature.expectation", true)
                                .set("feature.suppress_notloaded", true)
                )
                .setSource(new DirectorySource(build.getTargetFolder()))
                .build();

        for (Path testFolder : build.getTestFolders()) {
            for (File file : FileUtils.listFiles(testFolder.toFile(), new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
                try {
                    Validation validation = validator.validate(file);
                    validation.getReport().setFilename(file.toString());

                    build.addTestValidation(validation);
                    logger.info(String.format("%s (%s)", file, validation.getReport().getFlag()));
                } catch (Exception e) {
                    logger.warn(String.format("%s (%s)", file, e.getMessage()));
                }
            }
        }
    }
}
