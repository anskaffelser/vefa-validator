package no.difi.vefa.validator.api;

import java.util.List;

/**
 * An implementation of ValidatorPlugin defines a set of resources needed by the validator to support validation of a given kind of document types.
 */
public interface ValidatorPlugin {

    /**
     * Keywords representing the given kind of document type(s) supported.
     *
     * @return Keywords.
     */
    List<String> capabilities();

    /**
     * Checkers needed to validate the given kind of document type(s).
     *
     * @return Checkers implementing functionality.
     */
    List<Class<? extends Checker>> checkers();

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
}
