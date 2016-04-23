package no.difi.vefa.validator.api;

public interface Trigger {

    void check(Document document, Section section) throws ValidatorException;
}
