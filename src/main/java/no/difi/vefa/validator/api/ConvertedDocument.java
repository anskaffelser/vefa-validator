package no.difi.vefa.validator.api;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

/**
 * Representation of validation document where the document is converted before performing validation.
 */
public class ConvertedDocument extends Document {

    /**
     * Holding the original document.
     */
    private final ByteArrayInputStream source;

    /**
     * @param inputStream InputStream containing the document used during validation.
     * @param source InputStream containing the original document before converting.
     * @param declaration Declaration identifier used to recognize rules.
     * @param expectation Expectations when performing validation of triggered rules.
     */
    public ConvertedDocument(ByteArrayInputStream inputStream, ByteArrayInputStream source,
                             String declaration, Expectation expectation) {
        this(inputStream, source, Collections.singletonList(declaration), expectation);
    }

    /**
     * @param inputStream InputStream containing the document used during validation.
     * @param source InputStream containing the original document before converting.
     * @param declarations Declaration identifiers used to recognize rules.
     * @param expectation Expectations when performing validation of triggered rules.
     */
    public ConvertedDocument(ByteArrayInputStream inputStream, ByteArrayInputStream source,
                             List<String> declarations, Expectation expectation) {
        super(inputStream, declarations, expectation);
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
