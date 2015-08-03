package no.difi.vefa.validator.api;

import no.difi.vefa.validator.ValidatorException;

/**
 * Source for validation artifacts.
 */
public interface Source {

    /**
     * Instance of source with validation artifacts ready for use.
     *
     * @return Instance containing validation artifacts.
     */
    SourceInstance createInstance() throws ValidatorException;

}
