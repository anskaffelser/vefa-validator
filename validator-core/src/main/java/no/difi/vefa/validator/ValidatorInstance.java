package no.difi.vefa.validator;

import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.lang.UnknownDocumentTypeException;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.vefa.validator.trigger.TriggerFactory;
import no.difi.vefa.validator.util.CombinedFlagFilterer;
import no.difi.vefa.validator.util.DeclarationDetector;
import no.difi.vefa.validator.util.DeclarationIdentifier;
import no.difi.xsd.vefa.validator._1.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
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
    private ValidatorEngine validatorEngine;

    /**
     * Current validator configuration.
     */
    @Inject
    private Properties properties;

    /**
     * Declarations to use.
     */
    @Inject
    private DeclarationDetector declarationDetector;

    /**
     * Cache of checkers.
     */
    @Inject
    private LoadingCache<String, Checker> checkerCache;

    /**
     * Pool of presenters.
     */
    @Inject
    private LoadingCache<String, Renderer> rendererCache;

    /**
     * Trigger factory.
     */
    @Inject
    private TriggerFactory triggerFactory;

    /**
     * Normalized configurations indexed by document declarations.
     */
    private Map<String, Configuration> configurationMap = new HashMap<>();

    /**
     * List of packages supported by validator.
     *
     * @return List of packages.
     */
    protected List<PackageType> getPackages() {
        return validatorEngine.getPackages();
    }

    /**
     * Fetch properties for internal use.
     *
     * @return Current properties.
     */
    protected Properties getProperties() {
        return properties;
    }

    /**
     * Return validation configuration.
     *
     * @param declaration Fetch configuration using declaration.
     */
    protected Configuration getConfiguration(String declaration) throws UnknownDocumentTypeException {
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

    protected DeclarationIdentifier detect(byte[] content) {
        return declarationDetector.detect(content);
    }

    /**
     * Render document using stylesheet
     *
     * @param stylesheet   Stylesheet identifier from configuration.
     * @param document     Document used for styling.
     * @param outputStream Stream for dumping of result.
     */
    protected void render(StylesheetType stylesheet, Document document, Properties properties, OutputStream outputStream) throws ValidatorException {
        Renderer renderer;
        try {
            renderer = rendererCache.get(stylesheet.getIdentifier());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new ValidatorException(
                    String.format("Unable to borrow presenter object from pool for '%s'.", stylesheet.getIdentifier()), e);
        }

        renderer.render(document, new CombinedProperties(properties, this.properties), outputStream);
    }

    /**
     * Validate document using a file definition.
     *
     * @param fileType      File definition from configuration.
     * @param document      Document to validate.
     * @param configuration Complete configuration
     * @return Result of validation.
     */
    protected Section check(FileType fileType, Document document, Configuration configuration) throws ValidatorException {
        Checker checker;
        try {
            checker = checkerCache.get(fileType.getPath());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new ValidatorException(
                    String.format("Unable to get checker object from pool for '%s'.", configuration.getIdentifier()), e);
        }

        Section section = new Section(new CombinedFlagFilterer(configuration, document.getExpectation()));
        section.setFlag(FlagType.OK);

        if (properties.getBoolean("feature.infourl"))
            section.setInfoUrl(fileType.getInfoUrl());

        checker.check(document, section);

        section.setInfoUrl(null);
        return section;
    }

    /**
     * Validate document using a trigger definition.
     *
     * @param triggerType   Trigger definition from configuration.
     * @param document      Document to validate.
     * @param configuration Complete configuration
     * @return Result of validation.
     */
    protected Section trigger(TriggerType triggerType, Document document, Configuration configuration) throws ValidatorException {
        Section section = new Section(new CombinedFlagFilterer(configuration, document.getExpectation()));
        section.setFlag(FlagType.OK);
        triggerFactory.get(triggerType.getIdentifier()).check(document, section);
        return section;
    }

    @Override
    public void close() throws IOException {
        checkerCache.invalidateAll();
        checkerCache.cleanUp();

        rendererCache.invalidateAll();
        rendererCache.cleanUp();

        // This is last statement, allow to propagate.
        validatorEngine.close();
    }
}
