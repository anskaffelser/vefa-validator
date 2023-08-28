package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;

import java.io.InputStream;

public interface DeclarationWithChildren extends Declaration {

    Iterable<CachedFile> children(InputStream inputStream) throws ValidatorException;

}
