package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;

public interface Declaration {

    /**
     * Verify content to be of a given type.
     *
     * @param content Start of content
     * @param parent Parent identifier
     * @return Returns true if content is of given type.
     * @throws ValidatorException
     */
    boolean verify(byte[] content, String parent) throws ValidatorException;

    /**
     * Detect identifier representing standardId to be used for validation.
     *
     * @param content Start of content
     * @param parent Parent identifier
     * @return Returns standardId
     * @throws ValidatorException
     */
    String detect(byte[] content, String parent) throws ValidatorException;

    Expectation expectations(byte[] content) throws ValidatorException;

}
