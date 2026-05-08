package no.difi.vefa.validator.api;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

/**
 * Representation of validation document.
 */
public class Document {

    /**
     * Document as #ByteArrayInputStream.
     */
    private ByteArrayInputStream byteArrayInputStream;

    /**
     * Declaration identifier used to recognize rules.
     */
    private List<String> declarations;

    /**
     * Expectations when performing validation of triggered rules.
     */
    private Expectation expectation;

    public Document(ByteArrayInputStream inputStream) {
        this(inputStream, (String) null, null);
    }

    /**
     * @param inputStream InputStream containing the document used during validation.
     * @param declaration Declaration identifier used to recognize rules.
     * @param expectation Expectations when performing validation of triggered rules.
     */
    public Document(ByteArrayInputStream inputStream, String declaration, Expectation expectation)  {
        this(inputStream, Collections.singletonList(declaration), expectation);
    }

    /**
     * @param inputStream InputStream containing the document used during validation.
     * @param declarations Declaration identifiers used to recognize rules.
     * @param expectation Expectations when performing validation of triggered rules.
     */
    public Document(ByteArrayInputStream inputStream, List<String> declarations, Expectation expectation)  {
        this.byteArrayInputStream = inputStream;
        this.declarations = declarations;
        this.expectation = expectation;
    }

    /**
     * Declaration detected in document for validation.
     *
     * @return Declaration
     */
    public List<String> getDeclarations() {
        return declarations;
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
