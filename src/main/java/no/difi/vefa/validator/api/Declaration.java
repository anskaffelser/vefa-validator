package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;

import java.io.InputStream;
import java.util.List;

public interface Declaration {

    /**
     * Verify content to be of a given type.
     * @param content Start of content
     * @param parent  Parent identifier
     * @return Returns true if content is of given type.
     */
    boolean verify(byte[] content, List<String> parent) throws ValidatorException;

    /**
     * Detect identifier representing standardId to be used for validation.
     *
     * @param contentStream Content stream
     * @param contentStream The stream of the content
     * @param parent  Parent identifier
     * @return Returns standardId
     */
    List<String> detect(InputStream contentStream, List<String> parent) throws ValidatorException;

    Expectation expectations(byte[] content) throws ValidatorException;

}
