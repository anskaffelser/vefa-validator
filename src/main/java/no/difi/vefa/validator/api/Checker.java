package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.Document;

/**
 * Interface for classes performing validation of business documents.
 * <p/>
 * The constructor must contain no parameters.
 */
public interface Checker {

    void check(Document document, Section section) throws ValidatorException;

}
