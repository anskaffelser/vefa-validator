package no.difi.vefa.validator;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.xsd.vefa.validator._1.FileType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.PackageType;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains CheckerPools and Configuration, and is entry point for validation.
 */
class ValidatorInstance implements Closeable {

    /**
     * Logger
     */
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
    private Map<String, Configuration> configurationMap = new HashMap<>();

    /**
     * Declarations to use.
     */
    private Declaration[] declarations;

    /**
     * Pool of checkers.
     */
    private GenericKeyedObjectPool<String, Checker> checkerPool;

    /**
     * Pool of presenters.
     */
    private GenericKeyedObjectPool<String, Renderer> rendererPool;

    /**
     * Constructor loading artifacts and pools for validations.
     *
     * @param source Source for validation artifacts
     * @throws ValidatorException
     */
    ValidatorInstance(Source source, Properties properties, Class<? extends Checker>[] checkerImpls, Class<? extends Renderer>[] rendererImpls, Declaration[] declarations, Set<String> capabilities) throws ValidatorException {
        // Create config combined with default values.
        this.properties = new CombinedProperties(properties, ValidatorDefaults.PROPERTIES);

        // Create a new engine
        validatorEngine = new ValidatorEngine(source.createInstance(this.properties, capabilities));

        // Declarations
        this.declarations = declarations;

        // New pool for checkers
        checkerPool = new GenericKeyedObjectPool<>(new CheckerPoolFactory(validatorEngine, checkerImpls));
        checkerPool.setBlockWhenExhausted(this.properties.getBoolean("pools.checker.blockerWhenExhausted"));
        checkerPool.setLifo(this.properties.getBoolean("pools.checker.lifo"));
        checkerPool.setMaxTotal(this.properties.getInteger("pools.checker.maxTotal"));
        checkerPool.setMaxTotalPerKey(this.properties.getInteger("pools.checker.maxTotalPerKey"));

        // New pool for presenters
        rendererPool = new GenericKeyedObjectPool<>(new RendererPoolFactory(validatorEngine, rendererImpls));
        rendererPool.setBlockWhenExhausted(this.properties.getBoolean("pools.presenter.blockerWhenExhausted"));
        rendererPool.setLifo(this.properties.getBoolean("pools.presenter.lifo"));
        rendererPool.setMaxTotal(this.properties.getInteger("pools.presenter.maxTotal"));
        rendererPool.setMaxTotalPerKey(this.properties.getInteger("pools.presenter.maxTotalPerKey"));
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
     * Fetch properties for internal use.
     *
     * @return Current properties.
     */
    Properties getProperties() {
        return properties;
    }

    /**
     * Return validation configuration.
     *
     * @param declaration Fetch configuration using declaration.
     */
    Configuration getConfiguration(String declaration) throws ValidatorException {
        // Check cache of configurations is ready to use.
        if (configurationMap.containsKey(declaration))
            return configurationMap.get(declaration);

        // Create a new instance of configuration using the raw configuration.
        Configuration configuration = new Configuration(validatorEngine.getConfigurationByDeclaration(declaration));
        // Normalize configuration using inheritance declarations.
        configuration.normalize(validatorEngine);
        // Add configuration to map containing configurations ready to use.
        configurationMap.put(declaration, configuration);

        // Return configuration.
        return configuration;
    }

    Declaration[] getDeclarations() {
        return declarations;
    }

    /**
     * Render document using stylesheet
     *
     * @param stylesheet Stylesheet identifier from configuration.
     * @param document Document used for styling.
     * @param outputStream Stream for dumping of result.
     */
    void render(StylesheetType stylesheet, Document document, Properties properties, OutputStream outputStream) throws ValidatorException {
        Renderer renderer;
        try {
            renderer = rendererPool.borrowObject(stylesheet.getIdentifier());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new ValidatorException(
                    String.format("Unable to borrow presenter object from pool for '%s'.", stylesheet.getIdentifier()), e);
        }

        try {
            renderer.render(document, new CombinedProperties(properties, this.properties), outputStream);
        } finally {
            rendererPool.returnObject(stylesheet.getIdentifier(), renderer);
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
        int count = checkerPool.getNumActive() + checkerPool.getNumIdle();
        try {
            checker = checkerPool.borrowObject(fileType.getPath());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new ValidatorException(
                    String.format("Unable to borrow checker object from pool for '%s'.", configuration.getIdentifier()), e);
        }

        Section section = new Section(new CombinedFlagFilterer(configuration, document.getExpectation()));
        section.setFlag(FlagType.OK);

        try {
            checker.check(document, section);
        } finally {
            checkerPool.returnObject(fileType.getPath(), checker);
        }

        int newCount = checkerPool.getNumActive() + checkerPool.getNumIdle();
        if (count != newCount)
            logger.info("Num checerks: {}", newCount);

        return section;
    }

    @Override
    public void close() throws IOException {
        checkerPool.clear();
        rendererPool.clear();

        // This is last statement, allow to propagate.
        validatorEngine.close();
    }
}
