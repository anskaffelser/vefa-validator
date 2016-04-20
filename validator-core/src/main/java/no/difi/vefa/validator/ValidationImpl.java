package no.difi.vefa.validator;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.lang.UnknownDocumentTypeException;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Result of a validation.
 */
class ValidationImpl implements Validation {

    /**
     * Logger.
     */
    private static Logger logger = LoggerFactory.getLogger(ValidationImpl.class);

    private ValidatorInstance validatorInstance;

    private Properties properties;

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

    private Declaration declaration = null;

    private List<Validation> children;

    /**
     * Constructing new validator using validator instance and #InputStream containing document to validate.
     *
     * @param validatorInstance Instance of validator.
     * @param validationSource Source to validate.
     */
    ValidationImpl(ValidatorInstance validatorInstance, ValidationSource validationSource) {
        this.validatorInstance = validatorInstance;
        this.properties = new CombinedProperties(validationSource.getProperties(), validatorInstance.getProperties());

        this.report = new Report();
        this.report.setUuid(UUID.randomUUID().toString());
        this.report.setFlag(FlagType.OK);

        this.section.setTitle("Validator");
        this.section.setFlag(FlagType.OK);

        try {
            loadDocument(validationSource.getInputStream());
            loadConfiguration();
            nestedValidation();

            if (configuration != null)
                validate();
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
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

    private void loadDocument(InputStream inputStream) throws ValidatorException, IOException {
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
        byte[] bytes = new byte[10 * 1024];
        int length = byteArrayInputStream.read(bytes);

        if (length == -1)
            throw new IOException("Empty file");

        bytes = Arrays.copyOfRange(bytes, 0, length);

        // Use declaration implementations to detect declaration to use.
        for (Declaration d : validatorInstance.getDeclarations()) {
            if (d.verify(bytes)) {
                declaration = d;
                break;
            }
        }
        if (declaration == null)
            throw new UnknownDocumentTypeException("Unable to detect type of content.");

        // Detect expectation
        Expectation expectation = null;
        if (properties.getBoolean("feature.expectation")) {
            expectation = declaration.expectations(bytes);
            if (expectation != null)
                report.setDescription(expectation.getDescription());
        }

        if (declaration instanceof DeclarationWithConverter) {
            ByteArrayOutputStream convertedOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream.reset();
            ((DeclarationWithConverter) declaration).convert(byteArrayInputStream, convertedOutputStream);

            document = new ConvertedDocument(new ByteArrayInputStream(convertedOutputStream.toByteArray()), byteArrayInputStream, declaration.detect(bytes), expectation);
        } else {
            document = new Document(byteArrayInputStream, declaration.detect(bytes), expectation);
        }
    }

    private void loadConfiguration() throws UnknownDocumentTypeException {
        // Default values for report
        report.setTitle("Unknown document type");
        report.setFlag(FlagType.FATAL);

        // Get configuration using declaration
        this.configuration = validatorInstance.getConfiguration(document.getDeclaration());

        if (!properties.getBoolean("feature.suppress_notloaded"))
            for (String notLoaded : configuration.getNotLoaded())
                section.add("SYSTEM-007", String.format("Validation artifact '%s' not loaded.", notLoaded), FlagType.WARNING);

        // Update report using configuration for declaration
        report.setTitle(configuration.getTitle());
        report.setConfiguration(configuration.getIdentifier());
        report.setBuild(configuration.getBuild());
        report.setFlag(FlagType.OK);
    }

    private void validate() {
        long start = System.currentTimeMillis();

        for (FileType fileType : configuration.getFile()) {
            logger.debug("Validate: {}", fileType.getPath());

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
    private void nestedValidation() {
        if (report.getFlag().compareTo(FlagType.FATAL) < 0) {
            if (declaration instanceof DeclarationWithChildren && properties.getBoolean("feature.nesting")) {
                Iterable<InputStream> iterable = ((DeclarationWithChildren) declaration).children(document.getInputStream());
                for (InputStream inputStream : iterable) {
                    String filename = iterable instanceof IndexedIterator ? ((IndexedIterator) iterable).currentIndex() : null;
                    addChildValidation(new ValidationImpl(validatorInstance, new ValidationSourceImpl(inputStream)), filename);
                }
            }
        }
    }

    private void addChildValidation(Validation validation, String filename) {
        Report childReport = validation.getReport();
        childReport.setFilename(filename);
        report.getReport().add(childReport);

        if (children == null)
            children = new ArrayList<>();
        children.add(validation);
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
     * @param properties   Extra configuration to use for this rendering.
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
