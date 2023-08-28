package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;

import java.io.InputStream;
import java.io.OutputStream;

public interface DeclarationWithConverter extends Declaration {

    void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException;

}
