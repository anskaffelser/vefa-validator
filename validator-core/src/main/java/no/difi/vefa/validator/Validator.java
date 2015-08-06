package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Config;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.xsd.vefa.validator._1.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Validator containing an instance of validation configuration and validation artifacts.
 *
 * Validator is thread safe and should normally be created only once in a program.
 */
public class Validator {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(Validator.class);

    /**
     * Config
     */
    private Config config;

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
     * @see #validate(Path)
     */
    public Validation validate(File file) throws Exception {
        return validate(file.toPath());
    }

    /**
     * Validate file.
     *
     * @param file File to validate.
     * @return Validation result.
     * @throws Exception
     */
    public Validation validate(Path file) throws Exception {
        InputStream inputStream = Files.newInputStream(file);
        Validation validation = validate(inputStream);
        inputStream.close();
        return validation;
    }

    /**
     * Validate content of stream.
     *
     * @param inputStream Stream containing content.
     * @return Validation result.
     * @throws Exception
     */
    public Validation validate(InputStream inputStream) throws Exception {
        return new Validation(this.validatorInstance, inputStream);
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
     * @param config Configuration
     */
    public void setConfig(Config config) {
        this.config = config;
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
    void load() throws ValidatorException {
        try {
            // Make sure to default to repository source if no source is set.
            if (source == null)
                source = RepositorySource.forProduction();

            // Create a new instance based on source.
            validatorInstance = new ValidatorInstance(source.createInstance(), config);
        } catch (ValidatorException e) {
            logger.warn(e.getMessage(), e);

            // Exceptions during running is not a problem, but excpetion before the first validator is created is a problem.
            if (validatorInstance == null)
                throw new ValidatorException("Unable to load validator.", e);
        }
    }
}
