package no.difi.vefa.validator.tester;

import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.DirectorySource;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Tester implements Closeable {

    private static Logger logger = LoggerFactory.getLogger(Tester.class);

    public static void perform(Build build) throws ValidatorException {
        for (Validation validation : perform(build.getTargetFolder(), build.getTestFolders()))
            build.addTestValidation(validation);
    }

    public static List<Validation> perform(Path artifactsPath, List<Path> testPaths) throws ValidatorException {
        try (Tester tester = new Tester(artifactsPath)) {
            for (Path path : testPaths)
                tester.perform(path);
            return tester.finish();
        } catch (IOException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    public static List<Validation> perform(URI artifactsUri, List<Path> testPaths) throws ValidatorException {
        try (Tester tester = new Tester(artifactsUri)) {
            for (Path path : testPaths)
                tester.perform(path);
            return tester.finish();
        } catch (IOException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    private Validator validator;

    private List<Validation> validations = new ArrayList<>();

    private int tests;

    private int failed;

    private Tester(Path artifactsPath) throws ValidatorException {
        validator = ValidatorBuilder
                .newValidatorWithTest()
                .setProperties(new SimpleProperties()
                                .set("feature.nesting", true)
                                .set("feature.expectation", true)
                                .set("feature.suppress_notloaded", true)
                )
                .setSource(new DirectorySource(artifactsPath))
                .build();
    }

    private Tester(URI artifactsUri) throws ValidatorException {
        validator = ValidatorBuilder
                .newValidatorWithTest()
                .setProperties(new SimpleProperties()
                                .set("feature.nesting", true)
                                .set("feature.expectation", true)
                                .set("feature.suppress_notloaded", true)
                )
                .setSource(new RepositorySource(artifactsUri))
                .build();
    }

    private void perform(Path path) {
        for (File file : FileUtils.listFiles(path.toFile(), new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE))
            if (!file.getName().equals("buildconfig.xml"))
                validate(file);
    }

    private List<Validation> finish() {
        logger.info("{} tests performed, {} tests failed", tests, failed);

        return validations;
    }

    private void validate(File file) {
        try {
            Validation validation = validator.validate(file);
            validation.getReport().setFilename(file.toString());

            if ("http://difi.no/xsd/vefa/validator/1.0::testSet".equals(validation.getDocument().getDeclaration())) {
                logger.info("TestSet '{}'", file);

                for (int i = 0; i < validation.getChildren().size(); i++) {
                    Validation v = validation.getChildren().get(i);
                    v.getReport().setFilename(String.format("%s (%s)", file, i + 1));
                    append(v.getDocument().getExpectation().getDescription(), v, i + 1);
                }
            } else {
                append(file.toString(), validation, null);
            }
        } catch (IOException e) {
            logger.warn("Test '{}' ({})", file, e.getMessage(), e);
        }
    }

    public void append(String description, Validation validation, Integer numberInSet) {
        validations.add(validation);
        tests++;

        String prefix = numberInSet == null ? "" : "  ";

        if (validation.getReport().getFlag().compareTo(FlagType.EXPECTED) > 0) {
            if (numberInSet == null)
                logger.warn("{}Test '{}' ({})", prefix, description, validation.getReport().getFlag());
            else
                logger.warn("{}Test '{}) {}' ({})", prefix, numberInSet, description, validation.getReport().getFlag());
            failed++;

            for (SectionType sectionType : validation.getReport().getSection())
                for (AssertionType assertionType : sectionType.getAssertion())
                    if (assertionType.getFlag().compareTo(FlagType.EXPECTED) > 0)
                        logger.debug("{}  * {} {} ({})", prefix, assertionType.getIdentifier(), assertionType.getText(), assertionType.getFlag());
        } else if (numberInSet == null) {
            logger.info("Test '{}'", description);
        }
    }

    @Override
    public void close() throws IOException {
        if (validator != null) {
            validator.close();
            validator = null;
        }
    }
}
