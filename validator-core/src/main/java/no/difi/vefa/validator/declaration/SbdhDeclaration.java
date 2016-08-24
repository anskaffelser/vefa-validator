package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.DeclarationWithChildren;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

public class SbdhDeclaration extends AbstractXmlDeclaration implements DeclarationWithChildren {

    private static final String NAMESPACE = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";

    private static Logger logger = LoggerFactory.getLogger(SbdhDeclaration.class);

    @Override
    public boolean verify(byte[] content, String parent) throws ValidatorException {
        return parent.startsWith(NAMESPACE);
    }

    @Override
    public String detect(byte[] content, String parent) throws ValidatorException {
        // Simple stupid
        return "SBDH:1.0";
    }

    @Override
    public Iterable<InputStream> children(InputStream inputStream) {
        return new SbdhIterator(inputStream);
    }

    private class SbdhIterator implements Iterable<InputStream>, Iterator<InputStream> {

        private InputStream inputStream;
        private ByteArrayOutputStream outputStream;

        public SbdhIterator(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public Iterator<InputStream> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            if (inputStream == null)
                return false;

            try {
                outputStream = new ByteArrayOutputStream();

                XMLStreamReader source = xmlInputFactory.createXMLStreamReader(inputStream);
                XMLStreamWriter target = xmlOutputFactory.createXMLStreamWriter(outputStream, source.getEncoding());

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
                            payload = !source.getNamespaceURI().equals("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader");

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
                            payload = !source.getNamespaceURI().equals("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader");

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
                logger.warn(e.getMessage(), e);
            }
            inputStream = null;
            return outputStream != null;
        }

        @Override
        public InputStream next() {
            return new ByteArrayInputStream(outputStream.toByteArray());
        }

        @Override
        public void remove() {
            // No action
        }
    }
}
