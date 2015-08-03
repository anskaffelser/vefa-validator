package no.difi.vefa.validator;

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
    Document(InputStream inputStream) throws IOException {
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
        documentExpectation = new DocumentExpectation(content);
    }

    /**
     * Declaration detected in document for validation.
     *
     * @return Declaration
     */
    public DocumentDeclaration getDeclaration() {
        return documentDeclaration;
    }

    /**
     * Expectations detected in document for validation.
     *
     * @return Expectations
     */
    public DocumentExpectation getExpectation() {
        return documentExpectation;
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
