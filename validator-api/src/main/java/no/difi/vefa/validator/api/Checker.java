package no.difi.vefa.validator.api;

import java.nio.file.Path;

/**
 * Interface for classes performing validation of business documents.
 * <p/>
 * The constructor must contain no parameters.
 */
public interface Checker extends Trigger {
    void prepare(Path path) throws ValidatorException;
}
