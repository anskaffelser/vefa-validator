package no.dfo.anskaffelser.vefa.validator.api;

import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

/**
 * Source for validation artifacts.
 */
public interface Source {

    /**
     * Instance of source with validation artifacts ready for use.
     *
     * @throws ValidatorException
     * @return Instance containing validation artifacts.
     */
    SourceInstance createInstance(Properties properties) throws ValidatorException;

}
