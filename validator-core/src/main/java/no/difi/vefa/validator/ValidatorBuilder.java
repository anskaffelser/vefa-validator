package no.difi.vefa.validator;

import com.google.inject.Guice;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.module.CacheModule;
import no.difi.vefa.validator.module.ConfigModule;
import no.difi.vefa.validator.module.ValidatorModule;

/**
 * Builder supporting creation of validator.
 */
@Slf4j
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
     * @return Builder
     */
    public ValidatorBuilder setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Define source to use if other source then production repository to be used.
     *
     * @param source Source giving access to validation rules.
     * @return Builder
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
        return Guice.createInjector(new ValidatorModule(source, properties), new CacheModule(), new ConfigModule())
                .getInstance(Validator.class);
    }
}
