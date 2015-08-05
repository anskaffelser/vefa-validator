package no.difi.vefa.validator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define filestypes used by an implementation of Presenter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PresenterInfo {

    /**
     * List of filenames.
     */
    String[] value();
}
