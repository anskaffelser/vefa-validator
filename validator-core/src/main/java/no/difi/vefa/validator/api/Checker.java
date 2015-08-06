package no.difi.vefa.validator.api;

import no.difi.vefa.validator.Configuration;
import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.Section;
import no.difi.vefa.validator.ValidatorException;

import java.nio.file.Path;

public interface Checker {
    void prepare(Path path) throws ValidatorException;

    Section check(Document document, Configuration configuration) throws ValidatorException;
}
