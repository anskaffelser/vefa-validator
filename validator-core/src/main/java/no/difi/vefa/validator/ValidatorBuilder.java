package no.difi.vefa.validator;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.checker.XsdChecker;
import no.difi.vefa.validator.checker.XsltChecker;
import no.difi.vefa.validator.renderer.XsltRenderer;

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
     * Implementations of checker to use.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Checker>[] checkerImpls = new Class[] {
            XsltChecker.class,
            XsdChecker.class,
    };

    /**
     * Implementations of renderer to use.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Renderer>[] rendererImpls = new Class[] {
            XsltRenderer.class,
    };

    /**
     * Internal constructor, no action needed.
     */
    private ValidatorBuilder() {
        // No action
    }

    /**
     * Defines implementations of Checker to use.
     *
     * @param checkerImpls Implementations
     */
    public void setCheckerImpls(Class<? extends Checker>... checkerImpls) {
        this.checkerImpls = checkerImpls;
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
     * Defines implementations of Renderer to use.
     *
     * @param rendererImpls Implementations
     */
    public void setRendererImpls(Class<? extends Renderer>... rendererImpls) {
        this.rendererImpls = rendererImpls;
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
        validator.load(checkerImpls, rendererImpls);

        return validator;
    }
}
