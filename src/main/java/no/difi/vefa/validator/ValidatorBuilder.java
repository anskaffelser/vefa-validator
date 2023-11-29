package no.difi.vefa.validator;

import com.google.inject.Guice;
import no.difi.vefa.validator.api.Repository;
import no.difi.vefa.validator.model.Prop;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.util.Repositories;

import java.util.Objects;

/**
 * Builder supporting creation of validator.
 */
public class ValidatorBuilder {

    private Repository repository;

    private Prop[] props;

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
     * @param props Configuration
     * @return Builder object
     */
    public ValidatorBuilder setProperties(Prop... props) {
        this.props = props;
        return this;
    }

    /**
     * Define repository to use.
     *
     * @param repository Repository giving access to validation rules.
     * @return Builder object
     */
    public ValidatorBuilder setRepository(Repository repository) {
        this.repository = repository;
        return this;
    }

    /**
     * Initiate validator and return validator ready for use.
     *
     * @return Validator ready for use.
     */
    public Validator build() {
        if (Objects.isNull(repository))
            repository = Repositories.production();

        return Guice.createInjector(new ValidatorModule(repository, props)).getInstance(Validator.class);
    }
}
