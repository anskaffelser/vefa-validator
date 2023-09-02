package no.difi.vefa.validator.api;

import com.google.common.io.ByteStreams;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import no.difi.vefa.validator.lang.ValidatorException;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Representation of validation document.
 */
public class Document {

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    /**
     * Document as bytes.
     */
    private final byte[] bytes;

    /**
     * Declaration identifier used to recognize rules.
     */
    private final List<String> declarations;

    /**
     * Expectations when performing validation of triggered rules.
     */
    private final Expectation expectation;

    public static Document of(String content) {
        return of(content.getBytes(StandardCharsets.UTF_8));
    }

    public static Document ofResource(String resource) throws IOException {
        try (var inputStream = Document.class.getResourceAsStream(resource)) {
            return of(inputStream);
        }
    }

    public static Document of(File file) throws IOException {
        return of(file.toPath());
    }

    public static Document of(Path path) throws IOException {
        try (var inputStream = Files.newInputStream(path)) {
            return of(inputStream);
        }
    }

    public static Document of(InputStream inputStream) throws IOException {
        return of(ByteStreams.toByteArray(inputStream));
    }

    public static Document of(byte[] bytes) {
        return new Document(bytes, null, null);
    }

    public static Document of(Node node) throws ValidatorException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(baos));

            return of(baos.toByteArray());
        } catch (TransformerException e) {
            throw new ValidatorException("Unable to write XML", e);
        }
    }

    public Document(String content) {
        this(content.getBytes(StandardCharsets.UTF_8), null, null);
    }

    public Document(InputStream inputStream) throws IOException {
        this(inputStream, null, null);
    }

    /**
     * @param inputStream InputStream containing the document used during validation.
     * @param declaration Declaration identifier used to recognize rules.
     * @param expectation Expectations when performing validation of triggered rules.
     */
    public Document(ByteArrayInputStream inputStream, String declaration, Expectation expectation) throws IOException {
        this(inputStream, Collections.singletonList(declaration), expectation);
    }

    /**
     * @param inputStream  InputStream containing the document used during validation.
     * @param declarations Declaration identifiers used to recognize rules.
     * @param expectation  Expectations when performing validation of triggered rules.
     */
    public Document(InputStream inputStream, List<String> declarations, Expectation expectation) throws IOException {
        this(ByteStreams.toByteArray(inputStream), declarations, expectation);

        try {
            inputStream.reset();
        } catch (IOException e) {
            // No action
        }
    }

    public Document(byte[] bytes, List<String> declarations, Expectation expectation) {
        this.bytes = bytes;
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
    public ByteArrayInputStream asInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    public Document update(List<String> declarations, Expectation expectation) {
        return new Document(this.bytes, declarations, expectation);
    }

    public <T> T unmarshal(JAXBContext context, Class<T> cls) throws ValidatorException {
        try {
            return context.createUnmarshaller().unmarshal(new StreamSource(asInputStream()), cls).getValue();
        } catch (JAXBException e) {
            throw new ValidatorException("Unable to parse content.", e);
        }
    }

    public String toString() {
        return new String(bytes);
    }
}
