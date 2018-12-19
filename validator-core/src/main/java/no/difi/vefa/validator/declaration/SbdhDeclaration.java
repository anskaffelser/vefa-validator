package no.difi.vefa.validator.declaration;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.CachedFile;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.DeclarationWithChildren;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Type("xml.sbdh")
@MetaInfServices(Declaration.class)
public class SbdhDeclaration extends AbstractXmlDeclaration implements DeclarationWithChildren {

    private static final String NAMESPACE =
            "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return parent.get(0).startsWith(NAMESPACE);
    }

    @Override
    public List<String> detect(byte[] content, List<String> parent) {
        // Simple stupid
        return Collections.singletonList("SBDH:1.0");
    }

    @Override
    public Iterable<CachedFile> children(InputStream inputStream) {
        return new SbdhIterator(inputStream);
    }

    private class SbdhIterator implements Iterable<CachedFile>, Iterator<CachedFile> {

        private InputStream inputStream;

        private ByteArrayOutputStream outputStream;

        public SbdhIterator(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public Iterator<CachedFile> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            if (inputStream == null)
                return false;

            try {
                outputStream = new ByteArrayOutputStream();

                XMLStreamReader source = XML_INPUT_FACTORY.createXMLStreamReader(inputStream);
                XMLStreamWriter target = XML_OUTPUT_FACTORY.createXMLStreamWriter(outputStream, source.getEncoding());

                boolean payload = false;
                boolean written = false;

                do {
                    switch (source.getEventType()) {
                        case XMLStreamReader.START_DOCUMENT:
                            target.writeStartDocument(source.getEncoding(), source.getVersion());
                            break;

                        case XMLStreamConstants.END_DOCUMENT:
                            target.writeEndDocument();
                            break;

                        case XMLStreamConstants.START_ELEMENT:
                            payload = !source.getNamespaceURI()
                                    .equals("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader");

                            if (payload) {
                                written = true;
                                target.writeStartElement(source.getPrefix(), source.getLocalName(), source.getNamespaceURI());

                                for (int i = 0; i < source.getAttributeCount(); i++)
                                    target.writeAttribute(source.getAttributeLocalName(i), source.getAttributeValue(i));
                                for (int i = 0; i < source.getNamespaceCount(); i++)
                                    target.writeNamespace(source.getNamespacePrefix(i), source.getNamespaceURI(i));
                            }
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            payload = !source.getNamespaceURI()
                                    .equals("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader");

                            if (payload) {
                                written = true;
                                target.writeEndElement();
                            }
                            break;

                        case XMLStreamConstants.CHARACTERS:
                            if (payload) {
                                written = true;
                                target.writeCharacters(source.getText());
                            }
                            break;

                        case XMLStreamConstants.CDATA:
                            if (payload) {
                                written = true;
                                target.writeCData(source.getText());
                            }
                            break;
                    }

                    target.flush();

                } while (source.hasNext() && source.next() > 0);

                target.close();
                source.close();

                if (!written)
                    outputStream = null;
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            inputStream = null;
            return outputStream != null;
        }

        @Override
        public CachedFile next() {
            return new CachedFile(outputStream.toByteArray());
        }

        @Override
        public void remove() {
            // No action
        }
    }
}
