package no.difi.vefa.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Section;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.lang.UnknownDocumentTypeException;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.Detected;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.model.Prop;
import no.difi.vefa.validator.model.Props;
import no.difi.vefa.validator.service.CheckerService;
import no.difi.vefa.validator.service.ConfigurationService;
import no.difi.vefa.validator.service.DetectorService;
import no.difi.vefa.validator.util.Configuration;
import no.difi.xsd.vefa.validator._1.ConfigurationType;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.PackageType;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains CheckerPools and Configuration, and is entry point for validation.
 */
@Slf4j
@Singleton
class ValidatorInstance implements Closeable {

    /**
     * Instance of ValidatorEngine containing all raw content needed for validation.
     */
    @Inject
    private ConfigurationService validatorEngine;

    /**
     * Current validator configuration.
     */
    @Inject
    private Props props;

    /**
     * Declarations to use.
     */
    @Inject
    private DetectorService declarationDetector;

    /**
     * Cache of checkers.
     */
    @Inject
    private CheckerService checkerService;

    public Validation validate(Document document, Prop... props) {
        return ValidationInstance.of(this, document, props);
    }

    /**
     * Normalized configurations indexed by document declarations.
     */
    private final Map<String, Configuration> configurationMap = new HashMap<>();

    /**
     * List of packages supported by validator.
     *
     * @return List of packages.
     */
    protected final List<PackageType> getPackages() {
        return validatorEngine.getPackages();
    }

    /**
     * Fetch properties for internal use.
     *
     * @return Current properties.
     */
    protected final Props getProps() {
        return props;
    }

    /**
     * Return validation configuration.
     *
     * @param declarations Fetch configuration using declarations.
     */
    protected Configuration getConfiguration(List<String> declarations) throws UnknownDocumentTypeException {
        for (String declaration : declarations) {
            // Check cache of configurations is ready to use.
            if (configurationMap.containsKey(declaration))
                return configurationMap.get(declaration);

            ConfigurationType configurationType = validatorEngine.getConfigurationByDeclaration(declaration);

            if (configurationType != null) {
                // Create a new instance of configuration using the raw configuration.
                Configuration configuration = new Configuration(configurationType);

                // Normalize configuration using inheritance declarations.
                configuration.normalize(validatorEngine);
                // Add configuration to map containing configurations ready to use.
                configurationMap.put(declaration, configuration);

                // Return configuration.
                return configuration;
            }
        }

        throw new UnknownDocumentTypeException(String.format(
                "Configuration for '%s' not found.", declarations.get(0)));
    }

    protected Detected detect(Document document) throws IOException {
        return declarationDetector.detect(document);
    }


    /**
     * Validate document using a file definition.
     *
     * @param fileType      File definition from configuration.
     * @param document      Document to validate.
     * @param configuration Complete configuration
     * @return Result of validation.
     */
    protected Section check(FileType fileType, Document document, Configuration configuration)
            throws ValidatorException {
        Checker checker;
        try {
            checker = checkerService.get(fileType.getPath());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new ValidatorException(String.format(
                    "Unable to get checker object from pool for '%s'.", configuration.getIdentifier()), e);
        }

        Section section = new Section(document.getExpectation());
        section.setFlag(FlagType.OK);

        checker.check(document, section);
        return section;
    }

    @Override
    public void close() throws IOException {
        checkerService.clear();
    }
}
