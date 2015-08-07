package no.difi.vefa.validator.api;

import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.Section;

import java.nio.file.Path;

public interface Checker {
    void prepare(Path path) throws ValidatorException;

    void check(Document document, Section section) throws ValidatorException;
}
