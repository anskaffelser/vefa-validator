package no.difi.vefa.validator.api;

import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.List;

/**
 * An implementation of ValidatorPlugin defines a set of resources needed by the validator to support validation of a given kind of document types.
 */
public interface ValidatorPlugin {

    /**
     * Checkers needed to validate the given kind of document type(s).
     *
     * @return Checkers implementing functionality.
     */
    List<Class<? extends Checker>> checkers();

    /**
     * Triggers needed to validate the given kind of document type(s).
     *
     * @return Triggers implementing functionality.
     */
    List<Class<? extends Trigger>> triggers();

    /**
     * Defined declarations used to recognize and handle supported a kind of document type.
     *
     * @return Instances of declarations.
     */
    List<Declaration> declarations();

    /**
     * Renderers needed to render the given kind of document type(s).
     *
     * @return Renderers implementing functionality.
     */
    List<Class<? extends Renderer>> renderers();

    List<Configurations> configurations();
}
