package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;

/**
 * @author erlend
 */
public interface CheckerFactory {

    Checker prepare(ArtifactHolder artifactHolder, String path) throws ValidatorException;

}
