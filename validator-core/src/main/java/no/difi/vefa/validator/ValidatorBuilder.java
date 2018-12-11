package no.difi.vefa.validator;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.plugin.AsicePlugin;
import no.difi.vefa.validator.plugin.UblPlugin;
import no.difi.vefa.validator.plugin.ValidatorTestPlugin;
import no.difi.vefa.validator.util.DeclarationDetector;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder supporting creation of validator.
 */
@Slf4j
public class ValidatorBuilder {

    /**
     * Initiate creation of a new validator. Loads default plugins.
     *
     * @return Builder
     */
    public static ValidatorBuilder newValidator() {
        return emptyValidator()
                .plugin(UblPlugin.class)
                .plugin(AsicePlugin.class);
    }

    /**
     * Initiate creation of a new validator. Loads default plugins.
     *
     * @return Builder
     */
    public static ValidatorBuilder newValidatorWithTest() {
        return newValidator()
                .plugin(ValidatorTestPlugin.class);
    }

    /**
     * Initiate creation of a new validator.
     *
     * @return Builder
     */
    public static ValidatorBuilder emptyValidator() {
        Config config = ConfigFactory.load();
        config = config.withFallback(config.getConfig("defaults"));

        new DeclarationDetector(config);

        return new ValidatorBuilder();
    }

    /**
     * Validator to be delivered.
     */
    private Validator validator = new Validator();

    /**
     * Implementations of declarations to use.
     */
    private DeclarationDetector declarationDetector;

    /**
     * Implementations of checker to use.
     */
    private Set<Class<? extends Checker>> checkers = new HashSet<>();

    /**
     * Implementations of trigger to use.
     */
    private Set<Class<? extends Trigger>> triggers = new HashSet<>();

    /**
     * Implementations of renderer to use.
     */
    private Set<Class<? extends Renderer>> renderers = new HashSet<>();

    /**
     * Additional configurations.
     */
    private Set<Configurations> configurations = new HashSet<>();

    /**
     * Internal constructor, no action needed.
     */
    private ValidatorBuilder() {
        Config config = ConfigFactory.load();
        config = config.withFallback(config.getConfig("defaults"));

        declarationDetector = new DeclarationDetector(config);

        // No action
    }

    /**
     * Defines implementations of Checker to use.
     *
     * @param checkers Implementations
     * @return Builder
     */
    @SafeVarargs
    public final ValidatorBuilder checker(Class<? extends Checker>... checkers) {
        Collections.addAll(this.checkers, checkers);
        return this;
    }

    /**
     * Defines implementations of Renderer to use.
     *
     * @param renderers Implementations
     * @return Builder
     */
    @SafeVarargs
    public final ValidatorBuilder renderer(Class<? extends Renderer>... renderers) {
        Collections.addAll(this.renderers, renderers);
        return this;
    }

    @SafeVarargs
    public final ValidatorBuilder plugin(Class<? extends ValidatorPlugin>... plugins) {
        for (Class<? extends ValidatorPlugin> plugin : plugins) {
            try {
                plugin(plugin.newInstance());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return this;
    }

    public final ValidatorBuilder configuration(Configurations configurations) {
        this.configurations.add(configurations);
        return this;
    }

    public ValidatorBuilder plugin(ValidatorPlugin... plugins) {
        for (ValidatorPlugin plugin : plugins) {
            this.checkers.addAll(plugin.checkers());
            this.triggers.addAll(plugin.triggers());
            this.renderers.addAll(plugin.renderers());
            this.configurations.addAll(plugin.configurations());
        }
        return this;
    }

    /**
     * Defines configuration to use for validator.
     *
     * @param properties Configuration
     * @return Builder
     */
    public ValidatorBuilder setProperties(Properties properties) {
        this.validator.setProperties(properties);
        return this;
    }

    /**
     * Define source to use if other source then production repository to be used.
     *
     * @param source Source giving access to validation rules.
     * @return Builder
     */
    public ValidatorBuilder setSource(Source source) {
        this.validator.setSource(source);
        return this;
    }

    /**
     * Initiate validator and return validator ready for use.
     *
     * @return Validator ready for use.
     * @throws ValidatorException
     */
    @SuppressWarnings("unchecked")
    public Validator build() throws ValidatorException {
        validator.load(
                checkers.toArray(new Class[checkers.size()]),
                triggers.toArray(new Class[triggers.size()]),
                renderers.toArray(new Class[renderers.size()]),
                declarationDetector,
                configurations.toArray(new Configurations[configurations.size()])
        );

        return validator;
    }
}
