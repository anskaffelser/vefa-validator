package no.dfo.anskaffelser.vefa.validator.api;

import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

/**
 * @author erlend
 */
public interface CheckerFactory {

    Checker prepare(ArtifactHolder artifactHolder, String path) throws ValidatorException;

}
