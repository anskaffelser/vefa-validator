package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.ValidatorException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;

public class EspdDeclaration extends SimpleXmlDeclaration {

    public EspdDeclaration(String namespace, String localName) {
        super(namespace, localName);
    }

    @Override
    public String detect(byte[] content) throws ValidatorException {
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(content));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    if ("VersionID".equals(((StartElement) xmlEvent).getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            return String.format("%s::%s", super.detect(content), ((Characters) xmlEvent).getData());
                    }
                }
            }
        } catch (Exception e) {
            // No action.
        }

        return super.detect(content);
    }
}
