package no.difi.vefa.validator.api;

import no.difi.vefa.validator.Configuration;
import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.Section;

public interface Checker {

    Section check(Document document, Configuration configuration) throws Exception;

}
