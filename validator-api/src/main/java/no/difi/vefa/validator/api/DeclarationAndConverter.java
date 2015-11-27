package no.difi.vefa.validator.api;

import java.io.InputStream;
import java.io.OutputStream;

public interface DeclarationAndConverter extends Declaration {
    void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException;
}
