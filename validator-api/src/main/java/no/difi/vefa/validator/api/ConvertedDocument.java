package no.difi.vefa.validator.api;

import java.io.ByteArrayInputStream;

/**
 * Representation of validation document where the document is converted before performing validation.
 */
public class ConvertedDocument extends Document {

    /**
     * Holding the original document.
     */
    private ByteArrayInputStream source;

    /**
     * @param inputStream InputStream containing the document used during validation.
     * @param source InputStream containing the original document before converting.
     * @param declaration Declaration identifier used to recognize rules.
     * @param expectation Expectations when performing validation of triggered rules.
     */
    public ConvertedDocument(ByteArrayInputStream inputStream, ByteArrayInputStream source, String declaration, Expectation expectation) {
        super(inputStream, declaration, expectation);
        this.source = source;
    }

    /**
     * Returns the original document.
     *
     * @return Original document.
     */
    public ByteArrayInputStream getSource() {
        return source;
    }
}
