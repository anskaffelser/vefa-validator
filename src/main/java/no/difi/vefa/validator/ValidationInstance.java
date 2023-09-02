package no.difi.vefa.validator;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.lang.UnknownDocumentTypeException;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.vefa.validator.util.CombinedFlagFilterer;
import no.difi.vefa.validator.util.DeclarationIdentification;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Result of a validation.
 */
@Slf4j
class ValidationInstance implements Validation {

    private final ValidatorInstance validatorInstance;

    private final Properties properties;

    private Configuration configuration;

    /**
     * Final report.
     */
    private final Report report;

    /**
     * Section used to gather problems during validation.
     */
    private final Section section = new Section(new CombinedFlagFilterer());

    /**
     * Document subject to validation.
     */
    private Document document;

    private List<Validation> children;

    public static ValidationInstance of(ValidatorInstance validatorInstance, ValidationSource validationSource) {
        return new ValidationInstance(validatorInstance, validationSource);
    }

    /**
     * Constructing new validator using validator instance and validation source containing document to validate.
     *
     * @param validatorInstance Instance of validator.
     * @param validationSource  Source to validate.
     */
    private ValidationInstance(ValidatorInstance validatorInstance, ValidationSource validationSource) {
        this.validatorInstance = validatorInstance;
        this.properties = new CombinedProperties(validationSource.getProperties(), validatorInstance.getProperties());

        this.report = new Report();
        this.report.setUuid(UUID.randomUUID().toString());
        this.report.setFlag(FlagType.OK);

        this.section.setTitle("Validator");
        this.section.setFlag(FlagType.OK);

        try {
            var di = loadDocument(validationSource.getInputStream());
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

    private DeclarationIdentification loadDocument(InputStream inputStream) throws ValidatorException, IOException {
        // Read content to memory
        document = Document.of(inputStream);

        // Use declaration implementations to detect declaration to use.
        DeclarationIdentification declarationIdentifier = validatorInstance.detect(document);

        if (declarationIdentifier.equals(DeclarationIdentification.UNKNOWN))
            throw new UnknownDocumentTypeException("Unable to detect type of content.");

        // Detect expectation
        Expectation expectation = null;
        if (properties.getBoolean("feature.expectation")) {
            expectation = declarationIdentifier.expectations(document);

            if (expectation != null)
                report.setDescription(expectation.getDescription());
        }

        if (declarationIdentifier.hasConverted()) {
            document = declarationIdentifier.getConverted().update(declarationIdentifier.getFullIdentifier(), expectation);
        } else {
            document = document.update(declarationIdentifier.getFullIdentifier(), expectation);
        }

        return declarationIdentifier;
    }

    private void loadConfiguration() throws UnknownDocumentTypeException {
        // Default values for report
        report.setTitle("Unknown document type");
        report.setFlag(FlagType.FATAL);

        // Get configuration using declaration
        this.configuration = validatorInstance.getConfiguration(document.getDeclarations());

        if (!properties.getBoolean("feature.suppress_notloaded"))
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
    private void nestedValidation(DeclarationIdentification declarationIdentification) throws ValidatorException {
        if (report.getFlag().compareTo(FlagType.FATAL) < 0) {
            if (declarationIdentification.hasChildren() && properties.getBoolean("feature.nesting")) {
                for (Document child : declarationIdentification.getChildren()) {
                    addChildValidation(ValidationInstance.of(validatorInstance, new ValidationSourceImpl(child.asInputStream())));
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
