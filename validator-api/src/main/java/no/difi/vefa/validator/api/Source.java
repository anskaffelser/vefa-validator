package no.difi.vefa.validator.api;

import java.util.Set;

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
    SourceInstance createInstance(Properties properties, Set<String> capabilities) throws ValidatorException;

}
