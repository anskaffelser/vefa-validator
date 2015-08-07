package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Properties;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Representation of validation document.
 */
public class Document {

    private ByteArrayInputStream byteArrayInputStream;

    private DocumentDeclaration documentDeclaration;
    private DocumentExpectation documentExpectation;

    @SuppressWarnings("all")
    Document(InputStream inputStream, Properties properties) throws IOException {
        if (inputStream instanceof ByteArrayInputStream) {
            // Use stream as-is.
            byteArrayInputStream = (ByteArrayInputStream) inputStream;
        } else {
            // Convert stream to ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, byteArrayOutputStream);
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }

        // Read first 10kB for detections
        byte[] bytes = new byte[10*1024];
        getInputStream().read(bytes);
        String content = new String(bytes);

        // Detect declaration
        documentDeclaration = new DocumentDeclaration(content);

        // Detect expectation
        if (properties.getBoolean("feature.expectation"))
            documentExpectation = new DocumentExpectation(content);
    }

    /**
     * Declaration detected in document for validation.
     *
     * @return Declaration
     */
    public Declaration getDeclaration() {
        return documentDeclaration;
    }

    /**
     * Expectations detected in document for validation.
     *
     * @return Expectations
     */
    DocumentExpectation getDocumentExpectation() {
        return documentExpectation;
    }

    DocumentDeclaration getDocumentDeclaration() {
        return documentDeclaration;
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
