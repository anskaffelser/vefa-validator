package no.difi.vefa.validator.tester;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.model.Prop;
import no.difi.vefa.validator.module.PropertiesModule;
import no.difi.vefa.validator.module.SourceModule;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.service.TestingService;
import no.difi.vefa.validator.source.DirectorySource;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Singleton
public class Tester {

    @Inject
    private Validator validator;

    @Inject
    private TestingService testingService;

    private final List<Validation> validations = new ArrayList<>();

    private int tests;

    private int failed;

    public static List<Validation> perform(Path artifactsPath, List<Path> testPaths) throws IOException {
        return perform(new DirectorySource(artifactsPath), testPaths);
    }

    public static List<Validation> perform(URI artifactsUri, List<Path> testPaths) throws IOException {
        return perform(new RepositorySource(artifactsUri), testPaths);
    }

    public static List<Validation> perform(Source source, List<Path> testPaths) throws IOException {
        Tester tester = new Tester(source);

        for (Path path : testPaths)
            tester.perform(path);

        return tester.finish();
    }

    private Tester(Source source) {
        var modules = new ArrayList<Module>();
        modules.add(PropertiesModule.with(
                Prop.of("feature.nesting", true),
                Prop.of("feature.expectation", true),
                Prop.of("feature.suppress_notloaded", true)
        ));
        modules.add(new SourceModule(source));

        Guice.createInjector(Modules.override(new ValidatorModule()).with(modules))
                .injectMembers(this);
    }

    private void perform(Path path) throws IOException {
        Files.walk(path)
                .filter(p -> p.endsWith(".xml"))
                .filter(p -> !"buildconfig.xml".equals(p.toFile().getName()))
                .sorted()
                .forEach(this::validate);
    }

    private List<Validation> finish() {
        log.info("{} tests performed, {} tests failed", tests, failed);

        return validations;
    }

    private void validate(Path file) {
        try {
            // Load document
            var document = Document.of(file);

            Validation validation = validator.validate(document);
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
}
