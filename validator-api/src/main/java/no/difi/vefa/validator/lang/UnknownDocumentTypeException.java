package no.difi.vefa.validator.lang;

import no.difi.vefa.validator.api.ValidatorException;

public class UnknownDocumentTypeException extends ValidatorException {
    public UnknownDocumentTypeException(String message) {
        super(message);
    }
}
