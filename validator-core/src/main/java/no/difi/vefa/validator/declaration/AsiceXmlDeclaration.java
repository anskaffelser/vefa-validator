package no.difi.vefa.validator.declaration;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.vefa.validator.api.*;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

public class AsiceXmlDeclaration extends AbstractXmlDeclaration implements DeclarationWithConverter, DeclarationWithChildren {

    private static final String NAMESPACE = "urn:etsi.org:specification:02918:v1.2.1::asic";
    private static final String MIME = "application/vnd.etsi.asic-e+zip";

    private static final byte[] startsWith = new byte[]{0x50, 0x4B, 0x03, 0x04};

    @Override
    public boolean verify(byte[] content, String parent) throws ValidatorException {
        return NAMESPACE.equals(parent);
    }

    @Override
    public String detect(byte[] content, String parent) throws ValidatorException {
        return MIME;
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException {
        try {
            byte[] buffer = new byte[4];
            if (inputStream.read(buffer) != 4)
                throw new ValidatorException("Expected minimum 4 bytes.");

            if (Arrays.equals(buffer, startsWith)) {
                outputStream.write(buffer);
                ByteStreams.copy(inputStream, outputStream);
            } else {
                XMLStreamReader source = xmlInputFactory.createXMLStreamReader(new SequenceInputStream(new ByteArrayInputStream(buffer), inputStream));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                do {
                    if (source.getEventType() == XMLStreamConstants.CHARACTERS)
                        byteArrayOutputStream.write(source.getText().getBytes());
                } while (source.hasNext() && source.next() > 0);

                outputStream.write(BaseEncoding.base64().decode(CharMatcher.WHITESPACE.removeFrom(byteArrayOutputStream.toString())));
            }
        } catch (IOException | XMLStreamException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public Iterable<InputStream> children(InputStream inputStream) {
        try {
            return new AsicIterator(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    private class AsicIterator implements IndexedIterator<InputStream>, Iterable<InputStream> {

        private AsicReader asicReader;
        private String filename;

        public AsicIterator(InputStream inputStream) throws IOException {
            asicReader  = AsicReaderFactory.newFactory().open(inputStream);
        }

        @Override
        public Iterator<InputStream> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            try {
                return (filename = asicReader.getNextFile()) != null;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public InputStream next() {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                asicReader.writeFile(byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void remove() {
            // No action.
        }

        @Override
        public String currentIndex() {
            return filename;
        }
    }
}
