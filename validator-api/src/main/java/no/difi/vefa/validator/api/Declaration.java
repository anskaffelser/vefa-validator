package no.difi.vefa.validator.api;

public interface Declaration {

    /**
     * Verify content to be of a given type.
     *
     * @param content Start of content
     * @return Returns true if content is of given type.
     * @throws ValidatorException
     */
    boolean verify(byte[] content) throws ValidatorException;

    /**
     * Detect identifier representing standardId to be used for validation.
     *
     * @param content Start of content
     * @return Returns standardId
     * @throws ValidatorException
     */
    String detect(byte[] content) throws ValidatorException;

    Expectation expectations(byte[] content) throws ValidatorException;

}
