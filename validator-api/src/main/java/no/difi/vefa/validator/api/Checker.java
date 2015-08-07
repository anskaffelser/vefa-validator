package no.difi.vefa.validator.api;

import java.nio.file.Path;

public interface Checker {
    void prepare(Path path) throws ValidatorException;

    void check(Document document, Section section) throws ValidatorException;
}
