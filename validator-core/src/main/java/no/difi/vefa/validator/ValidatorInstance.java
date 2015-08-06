package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Presenter;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.PackageType;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains CheckerPools and Configuration, and is entry point for validation.
 */
class ValidatorInstance {

    private static Logger logger = LoggerFactory.getLogger(ValidatorInstance.class);

    /**
     * Instance of ValidatorEngine containing all raw content needed for validation.
     */
    private ValidatorEngine validatorEngine;

    /**
     * Current validator configuration.
     */
    private Properties properties;

    /**
     * Normalized configurations indexed by document declarations.
     */
    private Map<DocumentDeclaration, Configuration> configurationMap = new HashMap<>();

    /**
     * Pool of checkers.
     */
    private GenericKeyedObjectPool<String, Checker> checkerPool;

    /**
     * Pool of presenters.
     */
    private GenericKeyedObjectPool<String, Presenter> presenterPool;

    /**
     * Constructor loading artifacts and pools for validations.
     *
     * @param sourceInstance Source for validation artifacts
     * @throws ValidatorException
     */
    ValidatorInstance(SourceInstance sourceInstance, Properties properties) throws ValidatorException {
        // Create config combined with default values.
        this.properties = new CombinedProperties(properties, ValidatorDefaults.PROPERTIES);

        // Create a new engine
        validatorEngine = new ValidatorEngine(sourceInstance);

        // New pool for checkers
        checkerPool = new GenericKeyedObjectPool<>(new CheckerPoolFactory(validatorEngine));
        checkerPool.setBlockWhenExhausted(this.properties.getBoolean("pools.checker.blockerWhenExhausted"));
        checkerPool.setLifo(this.properties.getBoolean("pools.checker.lifo"));
        checkerPool.setMaxTotal(this.properties.getInteger("pools.checker.maxTotal"));
        checkerPool.setMaxTotalPerKey(this.properties.getInteger("pools.checker.maxTotalPerKey"));

        // New pool for presenters
        presenterPool = new GenericKeyedObjectPool<>(new PresenterPoolFactory(validatorEngine));
        presenterPool.setBlockWhenExhausted(this.properties.getBoolean("pools.presenter.blockerWhenExhausted"));
        presenterPool.setLifo(this.properties.getBoolean("pools.presenter.lifo"));
        presenterPool.setMaxTotal(this.properties.getInteger("pools.presenter.maxTotal"));
        presenterPool.setMaxTotalPerKey(this.properties.getInteger("pools.presenter.maxTotalPerKey"));
    }

    /**
     * List of packages supported by validator.
     *
     * @return List of packages.
     */
    List<PackageType> getPackages() {
        return validatorEngine.getPackages();
    }

    /**
     * Return validation configuration.
     *
     * @param documentDeclaration Fetch configuration using declaration.
     */
    Configuration getConfiguration(DocumentDeclaration documentDeclaration) throws ValidatorException {
        // Check cache of configurations is ready to use.
        if (configurationMap.containsKey(documentDeclaration))
            return configurationMap.get(documentDeclaration);

        // Create a new instance of configuration using the raw configuration.
        Configuration configuration = new Configuration(validatorEngine.getConfiguration(documentDeclaration));
        // Normalize configuration using inheritance declarations.
        configuration.normalize(validatorEngine);
        // Add configuration to map containing configurations ready to use.
        configurationMap.put(documentDeclaration, configuration);

        // Return confiuration.
        return configuration;
    }

    /**
     * Present document using stylesheet
     *
     * @param stylesheet Stylesheet identifier from configuration.
     * @param document Document used for styling.
     * @param outputStream Stream for dumping of result.
     */
    void present(StylesheetType stylesheet, Document document, Properties properties, OutputStream outputStream) throws ValidatorException {
        Presenter presenter;
        try {
            presenter = presenterPool.borrowObject(stylesheet.getIdentifier());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new ValidatorException(
                    String.format("Unable to borrow presenter object from pool for '%s'.", stylesheet.getIdentifier()), e);
        }

        try {
            presenter.present(document, new CombinedProperties(properties, this.properties), outputStream);
        } finally {
            presenterPool.returnObject(stylesheet.getIdentifier(), presenter);
        }
    }

    /**
     * Validate document using a file definition.
     *
     * @param fileType File definition from configuration.
     * @param document Document to validate.
     * @param configuration Complete configuration
     * @return Result of validation.
     */
    Section check(FileType fileType, Document document, Configuration configuration) throws ValidatorException {
        Checker checker;
        try {
            checker = checkerPool.borrowObject(fileType.getPath());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new ValidatorException(
                    String.format("Unable to borrow checker object from pool for '%s'.", configuration.getIdentifier()), e);
        }

        Section section;
        try {
            section = checker.check(document, configuration);
        } finally {
            checkerPool.returnObject(fileType.getPath(), checker);
        }

        return section;
    }
}
