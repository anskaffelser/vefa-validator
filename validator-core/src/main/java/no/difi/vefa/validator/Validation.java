package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Properties;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Result of a validation.
 */
public class Validation {

    /**
     * Logger.
     */
    private static Logger logger = LoggerFactory.getLogger(Validation.class);

    private ValidatorInstance validatorInstance;
    private Configuration configuration;

    /**
     * Final report.
     */
    private Report report;

    /**
     * Section used to gather problems during validation.
     */
    private Section section = new Section(null, null);

    /**
     * Document subject to validation.
     */
    private Document document;

    Validation(ValidatorInstance validatorInstance, InputStream inputStream) {
        long start = System.currentTimeMillis();
        this.validatorInstance = validatorInstance;

        this.report = new Report();
        this.report.setFlag(FlagType.OK);

        this.section.setTitle("Validator");
        this.section.setFlag(FlagType.OK);

        try {
            document = new Document(inputStream);
            report.setDescription(document.getExpectation().getDescription());

            loadConfiguration();

            if (configuration != null)
                validate();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        if (section.getAssertion().size() > 0) {
            for (AssertionType assertionType : section.getAssertion()) {
                if (assertionType.getFlag().compareTo(section.getFlag()) > 0)
                    section.setFlag(assertionType.getFlag());
            }
            report.getSection().add(0, section);

            if (section.getFlag().compareTo(getReport().getFlag()) > 0)
                getReport().setFlag(section.getFlag());
        }

        report.setRuntime((System.currentTimeMillis() - start) + "ms");
    }

    void loadConfiguration() {
        // Default values for report
        report.setTitle("Unknown document type");
        report.setFlag(FlagType.FATAL);

        // Verify presence of profileId
        if (document.getDeclaration().getProfileId() == null) {
            section.add("SYSTEM-001", "Unable to detect ProfileId.", FlagType.FATAL);
            return;
        }

        // Verify presence of customizationId
        if (document.getDeclaration().getCustomizationId() == null) {
            section.add("SYSTEM-002", "Unable to detect CustomizationId.", FlagType.FATAL);
            return;
        }

        // Get configuration using declaration
        try {
            this.configuration = validatorInstance.getConfiguration(document.getDeclaration());
        } catch (ValidatorException e) {
            // Add FATAL to report if validation artifacts for declaration is not found
            section.add("SYSTEM-003", "Unable to find validation configuration based on ProfileId and CustomizationId.", FlagType.FATAL);
            return;
        }

        for (String notLoaded : configuration.getNotLoaded())
            section.add("SYSTEM-007", String.format("Validation artifact '%s' not loaded.", notLoaded), FlagType.WARNING);

        // Update report using configuration for declaration
        report.setTitle(configuration.getTitle());
        report.setConfiguration(configuration.getIdentifier());
        report.setBuild(configuration.getBuild());
        report.setFlag(FlagType.OK);
    }

    void validate() throws Exception {
        for (FileType fileType : configuration.getFile()) {
            logger.debug("Validate: " + fileType.getPath());

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

        document.getExpectation().verify(section);
    }

    /**
     * Render document to a stream.
     *
     * @param outputStream Stream to use.
     * @throws Exception
     */
    public void present(OutputStream outputStream) throws Exception {
        present(outputStream, null);
    }

    /**
     * Render document to a stream, allows for extra configuration.
     *
     * @param outputStream Stream to use.
     * @param properties Extra configuration to use for this rendering.
     * @throws Exception
     */
    public void present(OutputStream outputStream, Properties properties) throws Exception {
        if (configuration.getStylesheet() == null)
            throw new ValidatorException("No stylesheet is defined for document type.");
        if (getReport().getFlag().equals(FlagType.FATAL))
            throw new ValidatorException(String.format("Status '%s' is not supported for rendering.", getReport().getFlag()));

        validatorInstance.present(configuration.getStylesheet(), document, properties, outputStream);
    }

    /**
     * Document used for validation as represented in the validator.
     *
     * @return Document object.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Report is the result of validation.
     *
     * @return Report
     */
    public Report getReport() {
        return report;
    }

}
