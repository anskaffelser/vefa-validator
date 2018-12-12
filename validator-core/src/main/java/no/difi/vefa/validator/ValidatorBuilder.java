package no.difi.vefa.validator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.module.CacheModule;
import no.difi.vefa.validator.module.ConfigModule;
import no.difi.vefa.validator.plugin.AsicePlugin;
import no.difi.vefa.validator.plugin.UblPlugin;
import no.difi.vefa.validator.plugin.ValidatorTestPlugin;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder supporting creation of validator.
 */
@Slf4j
public class ValidatorBuilder {

    private ValidatorBuilderModule module = new ValidatorBuilderModule();

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
        return new ValidatorBuilder();
    }

    /**
     * Internal constructor, no action needed.
     */
    private ValidatorBuilder() {
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
        Collections.addAll(module.checkers, checkers);
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
        Collections.addAll(module.renderers, renderers);
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
        module.configurations.add(configurations);
        return this;
    }

    public ValidatorBuilder plugin(ValidatorPlugin... plugins) {
        for (ValidatorPlugin plugin : plugins) {
            module.checkers.addAll(plugin.checkers());
            module.triggers.addAll(plugin.triggers());
            module.renderers.addAll(plugin.renderers());
            module.configurations.addAll(plugin.configurations());
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
        module.properties = properties;
        return this;
    }

    /**
     * Define source to use if other source then production repository to be used.
     *
     * @param source Source giving access to validation rules.
     * @return Builder
     */
    public ValidatorBuilder setSource(Source source) {
        module.source = source;
        return this;
    }

    /**
     * Initiate validator and return validator ready for use.
     *
     * @return Validator ready for use.
     */
    @SuppressWarnings("unchecked")
    public Validator build() {
        return Guice.createInjector(module, new CacheModule(), new ConfigModule())
                .getInstance(Validator.class);
    }

    private static class ValidatorBuilderModule extends AbstractModule {

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

        private Source source;

        private Properties properties;

        @Provides
        @Singleton
        public Set<Class<? extends Checker>> getCheckers() {
            return Collections.unmodifiableSet(checkers);
        }

        @Provides
        @Singleton
        public Set<Class<? extends Trigger>> getTriggers() {
            return Collections.unmodifiableSet(triggers);
        }

        @Provides
        @Singleton
        public Set<Class<? extends Renderer>> getRenderers() {
            return Collections.unmodifiableSet(renderers);
        }

        @Provides
        @Singleton
        public Set<Configurations> getConfigurations() {
            return Collections.unmodifiableSet(configurations);
        }

        @Provides
        @Singleton
        public SourceInstance getSource(Properties properties) throws ValidatorException {
            // Make sure to default to repository source if no source is set.
            return (source != null ? source : RepositorySource.forProduction()).createInstance(properties);
        }

        @Provides
        @Singleton
        public Properties getProperties() {
            // Create config combined with default values.
            return new CombinedProperties(properties, ValidatorDefaults.PROPERTIES);
        }
    }
}
