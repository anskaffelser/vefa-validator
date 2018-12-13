package no.difi.vefa.validator.api;

import java.nio.file.Path;

/**
 * @author erlend
 */
public interface CheckerFactory {

    Checker prepare(Path path) throws ValidatorException;

}
