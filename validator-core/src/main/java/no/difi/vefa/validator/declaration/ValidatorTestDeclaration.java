package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.DeclarationWithConverter;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ValidatorTestDeclaration extends XmlDeclaration implements DeclarationWithConverter {

    private static final String NAMESPACE = "http://difi.no/xsd/vefa/validator/1.0";

    private static Logger logger = LoggerFactory.getLogger(ValidatorTestDeclaration.class);

    @Override
    public boolean verify(byte[] content) throws ValidatorException {
        String c = new String(content);
        String namespace = XmlUtils.extractRootNamespace(c);
        return NAMESPACE.equals(namespace) && c.contains("<test ");
    }

    @Override
    public String detect(byte[] content) throws ValidatorException {
        try {
            XMLStreamReader source = xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(content));

            do {
                if (source.getEventType() == XMLStreamConstants.START_ELEMENT && source.getNamespaceURI().equals(NAMESPACE))
                    for (int i = 0; i < source.getAttributeCount(); i++)
                        if (source.getAttributeName(i).toString().equals("configuration"))
                            return String.format("configuration::%s", source.getAttributeValue(i));
            } while (source.hasNext() && source.next() > 0);
            return null;
        } catch (XMLStreamException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException {
        try {
            XMLStreamReader source = xmlInputFactory.createXMLStreamReader(inputStream);
            XMLStreamWriter target = xmlOutputFactory.createXMLStreamWriter(outputStream, source.getEncoding());

            boolean payload = false;

            do {
                switch (source.getEventType()) {
                    case XMLStreamReader.START_DOCUMENT:
                        logger.debug("START_DOCUMENT");
                        target.writeStartDocument(source.getEncoding(), source.getVersion());
                        break;

                    case XMLStreamConstants.END_DOCUMENT:
                        logger.debug("END_DOCUMENT");
                        target.writeEndDocument();
                        break;

                    case XMLStreamConstants.START_ELEMENT:
                        payload = !source.getNamespaceURI().equals(NAMESPACE);

                        if (payload) {
                            logger.debug("START_ELEMENT");
                            target.writeStartElement(source.getPrefix(), source.getLocalName(), source.getNamespaceURI());

                            for (int i = 0; i < source.getAttributeCount(); i++)
                                target.writeAttribute(source.getAttributeLocalName(i), source.getAttributeValue(i));
                            for (int i = 0; i < source.getNamespaceCount(); i++)
                                target.writeNamespace(source.getNamespacePrefix(i), source.getNamespaceURI(i));
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        payload = !source.getNamespaceURI().equals(NAMESPACE);

                        if (payload) {
                            logger.debug("END_ELEMENT");
                            target.writeEndElement();
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (payload) {
                            logger.debug("CHARACTERS");
                            target.writeCharacters(source.getText());
                        }
                        break;

                    case XMLStreamConstants.CDATA:
                        if (payload) {
                            logger.debug("CDATA");
                            target.writeCData(source.getText());
                        }
                        break;
                }

                target.flush();

            } while (source.hasNext() && source.next() > 0);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
}
