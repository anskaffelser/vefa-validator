package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.ArtifactHolder;

/**
 * @author erlend
 */
public interface CheckerFactory {

    String[] types();

    Checker prepare(ArtifactHolder artifactHolder, String path) throws ValidatorException;

}
