package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Config;
import no.difi.vefa.validator.api.Presenter;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.config.CombinedConfig;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.PackageType;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains CheckerPools and Configuration, and is entry point for validation.
 * One validator is loaded when initiating ValidatorInstance.
 */
class ValidatorInstance {

    private ValidatorEngine validatorEngine;
    private Config config;

    /**
     * Normalized configurations indexed by document declarations.
     */
    private Map<DocumentDeclaration, Configuration> configurationMap = new HashMap<>();

    private GenericKeyedObjectPool<String, Checker> checkerPool;
    private GenericKeyedObjectPool<String, Presenter> presenterPool;

    /**
     * Constructor loading artifacts and pools for validations.
     *
     * @param sourceInstance Source for validation artifacts
     * @throws ValidatorException
     */
    ValidatorInstance(SourceInstance sourceInstance, Config config) throws ValidatorException {
        // Create config combined with default values.
        this.config = new CombinedConfig(config, ValidatorDefaults.config);

        // Create a new engine
        validatorEngine = new ValidatorEngine(sourceInstance);

        // New pool for checkers
        checkerPool = new GenericKeyedObjectPool<>(new CheckerPoolFactory(validatorEngine));
        checkerPool.setBlockWhenExhausted(this.config.getBoolean("pools.checker.blockerWhenExhausted"));
        checkerPool.setLifo(this.config.getBoolean("pools.checker.lifo"));
        checkerPool.setMaxTotal(this.config.getInteger("pools.checker.maxTotal"));
        checkerPool.setMaxTotalPerKey(this.config.getInteger("pools.checker.maxTotalPerKey"));

        // New pool for presenters
        presenterPool = new GenericKeyedObjectPool<>(new PresenterPoolFactory(validatorEngine));
        presenterPool.setBlockWhenExhausted(this.config.getBoolean("pools.presenter.blockerWhenExhausted"));
        presenterPool.setLifo(this.config.getBoolean("pools.presenter.lifo"));
        presenterPool.setMaxTotal(this.config.getInteger("pools.presenter.maxTotal"));
        presenterPool.setMaxTotalPerKey(this.config.getInteger("pools.presenter.maxTotalPerKey"));
    }

    /**
     * List of packages supported by validator.
     *
     * @return List of packages.
     */
    public List<PackageType> getPackages() {
        return validatorEngine.getPackages();
    }

    /**
     * Return validation configuration.
     *
     * @param documentDeclaration Fetch configuration using declaration.
     */
    public Configuration getConfiguration(DocumentDeclaration documentDeclaration) throws ValidatorException {
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
    public void present(StylesheetType stylesheet, Document document, Config config, OutputStream outputStream) throws Exception {
        Presenter presenter = presenterPool.borrowObject(stylesheet.getIdentifier());
        presenter.present(document, new CombinedConfig(config, this.config), outputStream);
        presenterPool.returnObject(stylesheet.getIdentifier(), presenter);
    }

    /**
     * Validate document using a file definition.
     *
     * @param fileType File definition from configuration.
     * @param document Document to validate.
     * @param configuration Complete configuration
     * @return Result of validation.
     */
    public Section check(FileType fileType, Document document, Configuration configuration) throws Exception {
        Checker checker = checkerPool.borrowObject(fileType.getPath());
        Section section = checker.check(document, configuration);
        checkerPool.returnObject(fileType.getPath(), checker);
        return section;
    }
}
