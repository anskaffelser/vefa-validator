package no.difi.vefa.validator;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Module;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.module.ConfigurationModule;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Builder supporting creation of validator.
 */
public class ValidatorBuilder {

    private Source source;

    private Properties properties;

    /**
     * Initiate creation of a new validator. Loads default plugins.
     *
     * @return Builder object
     */
    public static ValidatorBuilder newValidator() {
        return new ValidatorBuilder();
    }

    /**
     * Internal constructor, no action needed.
     */
    private ValidatorBuilder() {
        // No action
    }

    /**
     * Defines configuration to use for validator.
     *
     * @param properties Configuration
     * @return Builder object
     */
    public ValidatorBuilder setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Define source to use if other source then production repository to be used.
     *
     * @param source Source giving access to validation rules.
     * @return Builder object
     */
    public ValidatorBuilder setSource(Source source) {
        this.source = source;
        return this;
    }

    /**
     * Initiate validator and return validator ready for use.
     *
     * @return Validator ready for use.
     */
    public Validator build() {
        List<Module> modules = Lists.newArrayList(ServiceLoader.load(Module.class));
        modules.add(new ConfigurationModule(source, properties));

        return Guice.createInjector(modules).getInstance(Validator.class);
    }
}
