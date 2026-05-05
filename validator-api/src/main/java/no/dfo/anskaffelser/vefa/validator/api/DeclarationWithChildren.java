package no.dfo.anskaffelser.vefa.validator.api;

import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

import java.io.InputStream;

public interface DeclarationWithChildren extends Declaration {

    Iterable<CachedFile> children(InputStream inputStream) throws ValidatorException;

}
