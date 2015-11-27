package no.difi.vefa.validator;

import com.google.common.reflect.ClassPath;
import no.difi.vefa.validator.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder supporting creation of validator.
 */
public class ValidatorBuilder {

    private static Logger logger = LoggerFactory.getLogger(ValidatorBuilder.class);

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
    private List<Declaration> declarations = new ArrayList<>();

    /**
     * Implementations of checker to use.
     */
    private List<Class<? extends Checker>> checkers = new ArrayList<>();

    /**
     * Implementations of renderer to use.
     */
    private List<Class<? extends Renderer>> renderers = new ArrayList<>();

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
    @Deprecated
    public ValidatorBuilder setCheckerImpls(Class<? extends Checker>... checkerImpls) {
        this.checkers.clear();
        Collections.addAll(this.checkers, checkerImpls);

        return this;
    }

    ValidatorBuilder loadCheckers(String... namespaces) {
        try {
            ClassPath classPath = ClassPath.from(getClass().getClassLoader());

            for (String namespace : namespaces) {
                for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(namespace)) {
                    logger.debug("Checker found: {}", classInfo.getName());
                    Class<?> cls = classInfo.load();
                    this.checkers.add((Class<? extends Checker>) cls);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return this;
    }

    @Deprecated
    ValidatorBuilder setDeclarations(Declaration... declarations) {
        this.declarations.clear();
        Collections.addAll(this.declarations, declarations);

        return this;
    }

    ValidatorBuilder loadDeclarations(String... namespaces) {
        try {
            ClassPath classPath = ClassPath.from(getClass().getClassLoader());

            for (String namespace : namespaces) {
                for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(namespace)) {
                    logger.debug("Declaration found: {}", classInfo.getName());
                    Class<?> cls = classInfo.load();
                    this.declarations.add((Declaration) cls.newInstance());
                }
            }
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }

        return this;
    }

    /**
     * Defines implementations of Renderer to use.
     *
     * @param rendererImpls Implementations
     * @return Builder
     */
    @Deprecated
    public ValidatorBuilder setRendererImpls(Class<? extends Renderer>... rendererImpls) {
        this.renderers.clear();
        Collections.addAll(this.renderers, rendererImpls);

        return this;
    }

    ValidatorBuilder loadRenderers(String... namespaces) {
        try {
            ClassPath classPath = ClassPath.from(getClass().getClassLoader());

            for (String namespace : namespaces) {
                for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses(namespace)) {
                    logger.debug("Renderer found: {}", classInfo.getName());
                    Class<?> cls = classInfo.load();
                    this.renderers.add((Class<? extends Renderer>) cls);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
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
        if (checkers.isEmpty())
            loadCheckers("no.difi.vefa.validator.checker");
        if (declarations.isEmpty())
            loadDeclarations("no.difi.vefa.validator.declaration");
        if (renderers.isEmpty())
            loadRenderers("no.difi.vefa.validator.renderer");

        validator.load(
                checkers.toArray(new Class[checkers.size()]),
                renderers.toArray(new Class[renderers.size()]),
                declarations.toArray(new Declaration[declarations.size()])
        );

        return validator;
    }
}
