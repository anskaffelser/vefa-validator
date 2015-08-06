package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Config;
import no.difi.vefa.validator.api.Source;

/**
 * Builder supporting creation of validator.
 */
public class ValidatorBuilder {

    /**
     * Initiate creation of a new validator.
     *
     * @return Builder
     * @throws ValidatorException
     */
    public static ValidatorBuilder newValidator() throws ValidatorException{
        return new ValidatorBuilder();
    }

    /**
     * Validator to be delivered.
     */
    private Validator validator = new Validator();

    /**
     * Internal constructor, no action needed.
     */
    private ValidatorBuilder() {
        // No action
    }

    /**
     * Defines configuration to use for validator.
     *
     * @param config Configuration
     */
    public ValidatorBuilder setConfig(Config config) {
        this.validator.setConfig(config);
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
    public Validator build() throws ValidatorException {
        validator.load();

        return validator;
    }
}
