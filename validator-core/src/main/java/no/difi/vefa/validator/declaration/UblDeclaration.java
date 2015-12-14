package no.difi.vefa.validator.declaration;

import com.sun.xml.internal.stream.events.CharacterEvent;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.util.XmlUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
public class UblDeclaration implements Declaration {

    private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    public boolean verify(String content) throws ValidatorException {
        String namespace = XmlUtils.extractRootNamespace(content);
        return namespace != null && namespace.startsWith("urn:oasis:names:specification:ubl:schema:xsd:");
    }

    public String detect(String content) throws ValidatorException {
        String customizationId = null;
        String profileId = null;

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(content.getBytes()));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = (StartElement) xmlEvent;

                    if ("CustomizationID".equals(startElement.getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof CharacterEvent)
                            customizationId = ((CharacterEvent) xmlEvent).getData();
                    }

                    if ("ProfileID".equals(startElement.getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof CharacterEvent)
                            profileId = ((CharacterEvent) xmlEvent).getData();

                        // ProfileID is the last in sequence.
                        return String.format("%s#%s", profileId, customizationId);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new ValidatorException(e.getMessage());
        }

        return null;
    }

    @Override
    public Expectation expectations(String content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
