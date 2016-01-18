package no.difi.vefa.validator;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.api.*;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Result of a validation.
 */
class ValidationImpl implements no.difi.vefa.validator.api.Validation {

    /**
     * Logger.
     */
    private static Logger logger = LoggerFactory.getLogger(ValidationImpl.class);

    private ValidatorInstance validatorInstance;
    private Configuration configuration;

    /**
     * Final report.
     */
    private Report report;

    /**
     * Section used to gather problems during validation.
     */
    private Section section = new Section(new CombinedFlagFilterer());

    /**
     * Document subject to validation.
     */
    private Document document;

    /**
     * Constructing new validator using validator instance and #InputStream containing document to validate.
     *
     * @param validatorInstance Instance of validator.
     * @param inputStream Document to validate.
     */
    ValidationImpl(ValidatorInstance validatorInstance, InputStream inputStream) {
        long start = System.currentTimeMillis();
        this.validatorInstance = validatorInstance;

        this.report = new Report();
        this.report.setFlag(FlagType.OK);

        this.section.setTitle("Validator");
        this.section.setFlag(FlagType.OK);

        try {
            loadDocument(inputStream);
            loadConfiguration();

            if (configuration != null)
                validate();
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        } catch (ValidatorException e) {
            section.add("SYSTEM-001", e.getMessage(), FlagType.FATAL);
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

    void loadDocument(InputStream inputStream) throws ValidatorException, IOException {
        ByteArrayInputStream byteArrayInputStream;
        if (inputStream instanceof ByteArrayInputStream) {
            // Use stream as-is.
            byteArrayInputStream = (ByteArrayInputStream) inputStream;
        } else {
            // Convert stream to ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteStreams.copy(inputStream, byteArrayOutputStream);
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }

        document = new Document(byteArrayInputStream, null, null);

        // Read first 10kB for detections
        byte[] bytes = new byte[10*1024];
        byteArrayInputStream.read(bytes);
        String content = new String(bytes).trim();

        // Use declaration implementations to detect declaration to use.
        Declaration declaration = null;
        for (Declaration d : validatorInstance.getDeclarations()) {
            if (d.verify(content)) {
                declaration = d;
                break;
            }
        }
        if (declaration == null)
            throw new ValidatorException("Unable to detect type of content.");

        // Detect expectation
        Expectation expectation = null;
        if (validatorInstance.getProperties().getBoolean("feature.expectation")) {
            expectation = declaration.expectations(content);
            if (expectation != null)
                report.setDescription(expectation.getDescription());
        }

        if (declaration instanceof DeclarationAndConverter) {
            ByteArrayOutputStream convertedOutputStream = new ByteArrayOutputStream();
            ((DeclarationAndConverter) declaration).convert(byteArrayInputStream, convertedOutputStream);

            document = new ConvertedDocument(new ByteArrayInputStream(convertedOutputStream.toByteArray()), byteArrayInputStream, declaration.detect(content), expectation);
        } else {
            document = new Document(byteArrayInputStream, declaration.detect(content), expectation);
        }
    }

    void loadConfiguration() {
        // Default values for report
        report.setTitle("Unknown document type");
        report.setFlag(FlagType.FATAL);

        // Get configuration using declaration
        try {
            this.configuration = validatorInstance.getConfiguration(document.getDeclaration());
        } catch (ValidatorException e) {
            // Add FATAL to report if validation artifacts for declaration is not found
            section.add("SYSTEM-003", String.format("Unable to find validation configuration based on identifier '%s'.", e.getMessage()), FlagType.FATAL);
            return;
        }

        if (!validatorInstance.getProperties().getBoolean("feature.suppress_notloaded"))
            for (String notLoaded : configuration.getNotLoaded())
                section.add("SYSTEM-007", String.format("Validation artifact '%s' not loaded.", notLoaded), FlagType.WARNING);

        // Update report using configuration for declaration
        report.setTitle(configuration.getTitle());
        report.setConfiguration(configuration.getIdentifier());
        report.setBuild(configuration.getBuild());
        report.setFlag(FlagType.OK);
    }

    void validate() {
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

        if (document.getExpectation() != null)
            document.getExpectation().verify(section);
    }

    /**
     * Render document to a stream.
     *
     * @param outputStream Stream to use.
     * @throws Exception
     */
    @Override
    public void render(OutputStream outputStream) throws Exception {
        render(outputStream, null);
    }

    /**
     * Render document to a stream, allows for extra configuration.
     *
     * @param outputStream Stream to use.
     * @param properties Extra configuration to use for this rendering.
     * @throws ValidatorException
     */
    @Override
    public void render(OutputStream outputStream, Properties properties) throws ValidatorException {
        if (getReport().getFlag().equals(FlagType.FATAL))
            throw new ValidatorException(String.format("Status '%s' is not supported for rendering.", getReport().getFlag()));
        if (configuration == null)
            throw new ValidatorException("Configuration was not detected, configuration is need for rendering.");
        if (configuration.getStylesheet() == null)
            throw new ValidatorException("No stylesheet is defined for document type.");

        validatorInstance.render(configuration.getStylesheet(), document, properties, outputStream);
    }

    /**
     * Returns true if validated document is renderable based upon same criteria as may be provide exception when using #render(...).
     *
     * @return 'true' if validated document is renderable.
     */
    @Override
    public boolean isRenderable() {
        return configuration != null && configuration.getStylesheet() != null && !getReport().getFlag().equals(FlagType.FATAL);
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

}
