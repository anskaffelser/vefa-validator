package no.difi.vefa.validator.api;

import java.nio.file.Path;

/**
 * Interface for classes performing validation of business documents.
 *
 * The constructor must contain no parameters.
 */
public interface Checker {
    void prepare(Path path) throws ValidatorException;

    void check(Document document, Section section) throws ValidatorException;
}
