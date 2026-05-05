package no.dfo.anskaffelser.vefa.validator.api;

import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

public interface Trigger {

    void check(Document document, Section section) throws ValidatorException;
    
}
