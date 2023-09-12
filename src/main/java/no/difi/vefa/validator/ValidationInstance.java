package no.difi.vefa.validator;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.FlagFilterer;
import no.difi.vefa.validator.api.Section;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.lang.UnknownDocumentTypeException;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.Detected;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.model.Prop;
import no.difi.vefa.validator.model.Props;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Result of a validation.
 */
@Slf4j
class ValidationInstance implements Validation {

    private final ValidatorInstance validatorInstance;

    private final Props props;

    private Configuration configuration;

    /**
     * Final report.
     */
    private final Report report;

    /**
     * Section used to gather problems during validation.
     */
    private final Section section = new Section(FlagFilterer.DEFAULT);

    /**
     * Document subject to validation.
     */
    private Document document;

    private List<Validation> children;

    public static ValidationInstance of(ValidatorInstance validatorInstance, Document document, Prop... props) {
        return new ValidationInstance(validatorInstance, document, props);
    }

    /**
     * Constructing new validator using validator instance and validation source containing document to validate.
     *
     * @param validatorInstance Instance of validator.
     * @param document  Source to validate.
     * @param props Properties for validation
     */
    private ValidationInstance(ValidatorInstance validatorInstance, Document document, Prop... props) {
        this.validatorInstance = validatorInstance;
        this.props = validatorInstance.getProps().update(props);

        this.report = new Report();
        this.report.setUuid(UUID.randomUUID().toString());
        this.report.setFlag(FlagType.OK);

        this.section.setTitle("Validator");
        this.section.setFlag(FlagType.OK);

        try {
            var di = loadDocument(document);
            loadConfiguration();
            nestedValidation(di);

            if (configuration != null)
                validate();
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        } catch (UnknownDocumentTypeException e) {
            section.add("SYSTEM-003", e.getMessage(), FlagType.UNKNOWN);
        } catch (ValidatorException e) {
            section.add("SYSTEM-001", e.getMessage(), FlagType.FATAL);
        }

        if (report.getTitle() == null)
            report.setTitle("Unknown document type");

        if (section.getAssertion().size() > 0) {
            for (AssertionType assertionType : section.getAssertion()) {
                if (assertionType.getFlag().compareTo(section.getFlag()) > 0)
                    section.setFlag(assertionType.getFlag());
            }
            report.getSection().add(0, section);

            if (section.getFlag().compareTo(getReport().getFlag()) > 0)
                getReport().setFlag(section.getFlag());
        }
    }

    private Detected loadDocument(Document document) throws ValidatorException, IOException {
        // Use declaration implementations to detect declaration to use.
        Detected declarationIdentifier = validatorInstance.detect(document);

        if (declarationIdentifier.equals(Detected.UNKNOWN))
            throw new UnknownDocumentTypeException("Unable to detect type of content.");

        // Detect expectation
        Expectation expectation = null;
        if (props.getBool("feature.expectation", false)) {
            expectation = declarationIdentifier.expectations(document);

            if (expectation != null)
                report.setDescription(expectation.getDescription());
        }

        if (declarationIdentifier.hasConverted()) {
            this.document = declarationIdentifier.getConverted().update(declarationIdentifier.getFullIdentifier(), expectation);
        } else {
            this.document = document.update(declarationIdentifier.getFullIdentifier(), expectation);
        }

        return declarationIdentifier;
    }

    private void loadConfiguration() throws UnknownDocumentTypeException {
        // Default values for report
        report.setTitle("Unknown document type");
        report.setFlag(FlagType.FATAL);

        // Get configuration using declaration
        this.configuration = validatorInstance.getConfiguration(document.getDeclarations());

        if (!props.getBool("feature.suppress_notloaded", false))
            for (String notLoaded : configuration.getNotLoaded())
                section.add("SYSTEM-007", String.format(
                        "Validation artifact '%s' not loaded.", notLoaded), FlagType.WARNING);

        // Update report using configuration for declaration
        report.setTitle(configuration.getTitle());
        report.setConfiguration(configuration.getIdentifier().getValue());
        report.setBuild(configuration.getBuild());
        report.setFlag(FlagType.OK);
    }

    private void validate() {
        long start = System.currentTimeMillis();

        for (FileType fileType : configuration.getFile()) {
            log.debug("Validate: {}", fileType.getPath());

            try {
                Section section = validatorInstance.check(fileType, document, configuration);
                section.setConfiguration(fileType.getConfiguration());
                section.setBuild(fileType.getBuild());
                report.getSection().add(section);

                if (section.getFlag().compareTo(getReport().getFlag()) > 0)
                    getReport().setFlag(section.getFlag());
            } catch (ValidatorException e) {
                this.section.add("SYSTEM-008", e.getMessage(), FlagType.ERROR);
            }

            if (getReport().getFlag().equals(FlagType.FATAL) || this.section.getFlag().equals(FlagType.FATAL))
                break;
        }

        if (document.getExpectation() != null)
            document.getExpectation().verify(section);

        report.setRuntime((System.currentTimeMillis() - start) + "ms");
    }

    /**
     * Handling nested validation.
     */
    private void nestedValidation(Detected detected) throws ValidatorException {
        if (report.getFlag().compareTo(FlagType.FATAL) < 0) {
            if (detected.hasChildren() && props.getBool("feature.nesting", false)) {
                for (Document child : detected.getChildren()) {
                    addChildValidation(ValidationInstance.of(validatorInstance, child));
                }
            }
        }
    }

    private void addChildValidation(Validation validation) {
        Report childReport = validation.getReport();
        report.getReport().add(childReport);

        if (children == null)
            children = new ArrayList<>();
        children.add(validation);
    }

    /**
     * Document used for validation as represented in the validator.
     *
     * @return Document object.
     */
    @Override
    public Document getDocument() {
        return document;
    }

    /**
     * Report is the result of validation.
     *
     * @return Report
     */
    @Override
    public Report getReport() {
        return report;
    }

    /**
     * Nested validations of validation.
     *
     * @return List of validations or null if none available.
     */
    @Override
    public List<Validation> getChildren() {
        return children;
    }
}
