package no.difi.vefa.validator.tester;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.DirectorySource;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Singleton
public class Tester implements Closeable {

    private Validator validator;

    private final List<Validation> validations = new ArrayList<>();

    private int tests;

    private int failed;

    public static List<Validation> perform(Path artifactsPath, List<Path> testPaths) {
        try (Tester tester = new Tester(artifactsPath)) {
            for (Path path : testPaths)
                tester.perform(path);
            return tester.finish();
        }
    }

    public static List<Validation> perform(URI artifactsUri, List<Path> testPaths) {
        try (Tester tester = new Tester(artifactsUri)) {
            for (Path path : testPaths)
                tester.perform(path);
            return tester.finish();
        }
    }

    private Tester(Path artifactsPath) {
        validator = ValidatorBuilder
                .newValidator()
                .setProperties(new SimpleProperties()
                        .set("feature.nesting", true)
                        .set("feature.expectation", true)
                        .set("feature.suppress_notloaded", true)
                )
                .setSource(new DirectorySource(artifactsPath))
                .build();
    }

    private Tester(URI artifactsUri) {
        validator = ValidatorBuilder
                .newValidator()
                .setProperties(new SimpleProperties()
                        .set("feature.nesting", true)
                        .set("feature.expectation", true)
                        .set("feature.suppress_notloaded", true)
                )
                .setSource(new RepositorySource(artifactsUri))
                .build();
    }

    private void perform(Path path) {
        List<File> files = new ArrayList<>(FileUtils.listFiles(
                path.toFile(), new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE));
        Collections.sort(files);

        for (File file : files)
            if (!file.getName().equals("buildconfig.xml"))
                validate(file);
    }

    private List<Validation> finish() {
        log.info("{} tests performed, {} tests failed", tests, failed);

        return validations;
    }

    private void validate(File file) {
        try {
            Validation validation = validator.validate(file);
            validation.getReport().setFilename(file.toString());

            if (validation.getDocument().getDeclarations()
                    .contains("xml.testset::http://difi.no/xsd/vefa/validator/1.0::testSet")) {
                log.info("TestSet '{}'", file);

                for (int i = 0; i < validation.getChildren().size(); i++) {
                    Validation v = validation.getChildren().get(i);
                    v.getReport().setFilename(String.format("%s (%s)", file, i + 1));
                    append(v.getDocument().getExpectation().getDescription(), v, i + 1);
                }
            } else {
                append(file.toString(), validation, null);
            }
        } catch (NullPointerException e) {
            log.warn("File '{}' ({})", file, "Unable to parse file - please make sure it contains valid xml.");
        } catch (IOException e) {
            log.warn("Test '{}' ({})", file, e.getMessage(), e);
        }
    }

    public void append(String description, Validation validation, Integer numberInSet) {
        validations.add(validation);
        tests++;

        description = description.replaceAll("[ \\t\\r\\n]+", " ");

        String prefix = numberInSet == null ? "" : "  ";

        if (validation.getReport().getFlag().compareTo(FlagType.EXPECTED) > 0) {
            log.warn("{}Test '{}' ({})", prefix, description, validation.getReport().getFlag());
            failed++;

            for (SectionType sectionType : validation.getReport().getSection())
                for (AssertionType assertionType : sectionType.getAssertion())
                    if (assertionType.getFlag().compareTo(FlagType.EXPECTED) > 0)
                        log.info("{}  * {} {} ({})", prefix, assertionType.getIdentifier(),
                                assertionType.getText(), assertionType.getFlag());
        } else if (numberInSet == null) {
            log.info("Test '{}'", description);
        }
    }

    @Override
    public void close() {
        if (validator != null) {
            validator.close();
            validator = null;
        }
    }
}
