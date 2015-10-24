package no.difi.vefa.validator;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.checker.SvrlXsltChecker;
import no.difi.vefa.validator.checker.XsdChecker;
import no.difi.vefa.validator.declaration.UblDeclaration;
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
     * Implementations of declarations to use.
     */
    @SuppressWarnings("unchecked")
    private Declaration[] declarationImpls = new Declaration[] {
            new UblDeclaration()
    };

    /**
     * Implementations of checker to use.
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Checker>[] checkerImpls = new Class[] {
            SvrlXsltChecker.class,
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
     * @return Builder
     */
    public ValidatorBuilder setCheckerImpls(Class<? extends Checker>... checkerImpls) {
        this.checkerImpls = checkerImpls;
        return this;
    }

    ValidatorBuilder setDeclarations(Declaration... declarations) {
        this.declarationImpls = declarations;
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
     * Defines implementations of Renderer to use.
     *
     * @param rendererImpls Implementations
     * @return Builder
     */
    public ValidatorBuilder setRendererImpls(Class<? extends Renderer>... rendererImpls) {
        this.rendererImpls = rendererImpls;
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
        validator.load(checkerImpls, rendererImpls, declarationImpls);

        return validator;
    }
}
