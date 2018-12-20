package no.difi.vefa.validator.declaration;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteStreams;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.lang.ValidatorException;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Type("xml.asice")
@MetaInfServices(Declaration.class)
public class AsiceXmlDeclaration extends AbstractXmlDeclaration
        implements DeclarationWithConverter, DeclarationWithChildren {

    private static final String NAMESPACE = "urn:etsi.org:specification:02918:v1.2.1::asic";

    private static final String MIME = "application/vnd.etsi.asic-e+zip";

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return NAMESPACE.equals(parent.get(0));
    }

    @Override
    public List<String> detect(byte[] content, List<String> parent) {
        return Collections.singletonList(MIME);
    }

    @Override
    public Expectation expectations(byte[] content) {
        return null;
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException {
        try {
            XMLStreamReader source = XML_INPUT_FACTORY.createXMLStreamReader(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            do {
                if (source.getEventType() == XMLStreamConstants.CHARACTERS)
                    byteArrayOutputStream.write(source.getText().getBytes());
            } while (source.hasNext() && source.next() > 0);

            outputStream.write(BaseEncoding.base64().decode(
                    CharMatcher.whitespace().removeFrom(byteArrayOutputStream.toString())));
        } catch (IOException | XMLStreamException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public Iterable<CachedFile> children(InputStream inputStream) {
        try {
            AsicReader asicReader = AsicReaderFactory.newFactory().open(inputStream);
            List<CachedFile> files = new ArrayList<>();

            String filename;
            while ((filename = asicReader.getNextFile()) != null) {
                files.add(CachedFile.of(filename, ByteStreams.toByteArray(asicReader.inputStream())));
            }

            return files;
        } catch (IOException e) {
            return null;
        }
    }
}
