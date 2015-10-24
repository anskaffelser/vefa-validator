package no.difi.vefa.validator.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Representation of validation document.
 */
public class Document {

    /**
     * Document as #ByteArrayInputStream.
     */
    private ByteArrayInputStream byteArrayInputStream;
    private String declaration;
    private Expectation expectation;

    @SuppressWarnings("all")
    public Document(ByteArrayInputStream inputStream, String declaration, Expectation expectation) throws IOException, ValidatorException {
        this.byteArrayInputStream = inputStream;
        this.declaration = declaration;
        this.expectation = expectation;
    }

    /**
     * Declaration detected in document for validation.
     *
     * @return Declaration
     */
    public String getDeclaration() {
        return declaration;
    }

    /**
     * Expectations detected in document for validation.
     *
     * @return Expectations
     */
    public Expectation getExpectation() {
        return expectation;
    }

    /**
     * Helper returning validated document as ByteArrayInputStream ready for use.
     *
     * @return Validated document
     */
    public ByteArrayInputStream getInputStream() {
        byteArrayInputStream.reset();
        return byteArrayInputStream;
    }
}
