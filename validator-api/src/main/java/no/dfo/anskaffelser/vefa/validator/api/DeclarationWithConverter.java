package no.dfo.anskaffelser.vefa.validator.api;

import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

import java.io.InputStream;
import java.io.OutputStream;

public interface DeclarationWithConverter extends Declaration {

    void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException;

}
