package no.difi.vefa.validator;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.vefa.validator.util.DeclarationDetector;
import no.difi.xsd.vefa.validator._1.Configurations;
import no.difi.xsd.vefa.validator._1.PackageType;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Validator containing an instance of validation configuration and validation artifacts.
 * <p/>
 * Validator is thread safe and should normally be created only once in a program.
 */
@Slf4j
public class Validator implements Closeable {

    /**
     * Config
     */
    private Properties properties;

    /**
     * Source
     */
    private Source source;

    /**
     * Current validator instance.
     */
    private ValidatorInstance validatorInstance;

    /**
     * Constructor
     */
    Validator() {
        // No action
    }

    /**
     * Validate file.
     *
     * @param file File to validate.
     * @return Validation result.
     * @throws IOException
     */
    public Validation validate(File file) throws IOException {
        return validate(file.toPath());
    }

    /**
     * Validate file.
     *
     * @param file File to validate.
     * @return Validation result.
     * @throws IOException
     */
    public Validation validate(Path file) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file)) {
            return validate(inputStream);
        }
    }

    /**
     * Validate content of stream.
     *
     * @param inputStream Stream containing content.
     * @return Validation result.
     */
    public Validation validate(InputStream inputStream) {
        return validate(new ValidationSourceImpl(inputStream));
    }

    /**
     * Validate content of stream.
     *
     * @param inputStream Stream containing content.
     * @param properties  Properties used for individual validation.
     * @return Validation result.
     */
    public Validation validate(InputStream inputStream, Properties properties) {
        return validate(new ValidationSourceImpl(inputStream, properties));
    }

    /**
     * Validate content of packaged stream.
     *
     * @param validationSource Package containing source.
     * @return Validation result.
     */
    public Validation validate(ValidationSource validationSource) {
        return new ValidationImpl(this.validatorInstance, validationSource);
    }

    /**
     * Validate file from filePath string
     *
     * @param filePath string representing filePath
     * @return Validation result
     * @throws IOException
     */
    public Validation validate(String filePath) throws IOException {
        return validate(Paths.get(filePath));
    }

    /**
     * List of packages supported by validator.
     *
     * @return List of packages.
     */
    public List<PackageType> getPackages() {
        return this.validatorInstance.getPackages();
    }

    /**
     * Set configuration for validator.
     *
     * @param properties Configuration
     */
    void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Set source for validator.
     *
     * @param source Source
     */
    void setSource(Source source) {
        this.source = source;
    }

    /**
     * Creates a new instance of ValidatorInstance for use.
     *
     * @throws ValidatorException
     */
    void load(Class<? extends Checker>[] checkerImpls, Class<? extends Trigger>[] triggerImpls, Class<? extends Renderer>[] rendererImpls, DeclarationDetector declarationDetector, Configurations[] configurations) throws ValidatorException {
        try {
            // Make sure to default to repository source if no source is set.
            if (source == null)
                source = RepositorySource.forProduction();

            // Create a new instance based on source.
            validatorInstance = new ValidatorInstance(source, properties, checkerImpls, triggerImpls, rendererImpls, declarationDetector, configurations);
        } catch (ValidatorException e) {
            log.error(e.getMessage(), e);

            // Exceptions during running is not a problem, but exception before the first validator is created is a problem.
            if (validatorInstance == null)
                throw new ValidatorException("Unable to load validator.", e);
        }
    }

    @Override
    public void close() {
        try {
            if (validatorInstance != null)
                validatorInstance.close();
        } catch (IOException e) {
            log.warn("Exception when closing Validator: {}", e.getMessage(), e);
        } finally {
            validatorInstance = null;
        }
    }
}
