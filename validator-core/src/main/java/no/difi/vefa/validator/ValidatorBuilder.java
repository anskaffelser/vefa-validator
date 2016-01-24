package no.difi.vefa.validator;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.plugin.SbdhPlugin;
import no.difi.vefa.validator.plugin.UblPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder supporting creation of validator.
 */
public class ValidatorBuilder {

    private static Logger logger = LoggerFactory.getLogger(ValidatorBuilder.class);

    /**
     * Initiate creation of a new validator. Loads default plugins.
     *
     * @return Builder
     */
    public static ValidatorBuilder newValidator() {
        return emptyValidator()
                .plugin(UblPlugin.class)
                .plugin(SbdhPlugin.class);
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
     * Validator to be delivered.
     */
    private Validator validator = new Validator();

    private Set<String> capabilities = new HashSet<>();

    /**
     * Implementations of declarations to use.
     */
    private Set<Declaration> declarations = new HashSet<>();

    /**
     * Implementations of checker to use.
     */
    private Set<Class<? extends Checker>> checkers = new HashSet<>();

    /**
     * Implementations of renderer to use.
     */
    private Set<Class<? extends Renderer>> renderers = new HashSet<>();

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
        Collections.addAll(this.checkers, checkers);
        return this;
    }

    @Deprecated
    public ValidatorBuilder setCheckerImpls(Class<? extends Checker>... checkerImpls) {
        this.checkers.clear();
        return checker(checkerImpls);
    }

    public ValidatorBuilder declaration(Declaration... declarations) {
        Collections.addAll(this.declarations, declarations);
        return this;
    }

    @Deprecated
    ValidatorBuilder setDeclarations(Declaration... declarations) {
        this.declarations.clear();
        return declaration(declarations);
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

    @Deprecated
    public ValidatorBuilder setRendererImpls(Class<? extends Renderer>... rendererImpls) {
        this.renderers.clear();
        return renderer(rendererImpls);
    }

    @SafeVarargs
    public final ValidatorBuilder plugin(Class<? extends ValidatorPlugin>... plugins) {
        for (Class<? extends ValidatorPlugin> plugin : plugins) {
            try {
                plugin(plugin.newInstance());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return this;
    }

    public ValidatorBuilder plugin(ValidatorPlugin... plugins) {
        for (ValidatorPlugin plugin : plugins) {
            this.capabilities.addAll(plugin.capabilities());
            this.checkers.addAll(plugin.checkers());
            this.declarations.addAll(plugin.declarations());
            this.renderers.addAll(plugin.renderers());
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
                renderers.toArray(new Class[renderers.size()]),
                declarations.toArray(new Declaration[declarations.size()]),
                capabilities
        );

        return validator;
    }
}
