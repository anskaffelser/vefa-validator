package no.difi.vefa.validator.api;

import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.ValidatorException;

import java.io.OutputStream;

public interface Presenter {

    void present(Document document, OutputStream outputStream) throws ValidatorException;

}
