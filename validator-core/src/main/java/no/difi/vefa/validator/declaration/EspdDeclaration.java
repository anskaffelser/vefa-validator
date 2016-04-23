package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.util.XmlUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;

public class EspdDeclaration extends XmlDeclaration {

    private String namespace;
    private String localName;

    public EspdDeclaration(String namespace, String localName) {
        this.namespace = namespace;
        this.localName = localName;
    }

    @Override
    public boolean verify(byte[] content) throws ValidatorException {
        String c = new String(content);
        return namespace.equals(XmlUtils.extractRootNamespace(c)) && localName.equals(XmlUtils.extractLocalName(c));
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
                            return String.format("%s::%s::%s", namespace, localName, ((Characters) xmlEvent).getData());
                    }
                }
            }
        } catch (Exception e) {
            // No action.
        }

        return String.format("%s::%s", namespace, localName);
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
