package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.util.XmlUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
public class UblDeclaration implements Declaration {

    private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    public boolean verify(byte[] content) throws ValidatorException {
        String namespace = XmlUtils.extractRootNamespace(new String(content));
        return namespace != null && namespace.startsWith("urn:oasis:names:specification:ubl:schema:xsd:");
    }

    public String detect(byte[] content) throws ValidatorException {
        String customizationId = null;
        String profileId = null;

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(content));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = (StartElement) xmlEvent;

                    if ("CustomizationID".equals(startElement.getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            customizationId = ((Characters) xmlEvent).getData();
                    }

                    if ("ProfileID".equals(startElement.getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            profileId = ((Characters) xmlEvent).getData();

                        // ProfileID is the last in sequence.
                        return String.format("%s#%s", profileId, customizationId);
                    }
                }
            }
        } catch (Exception e) {
            // No action.
        }

        throw new ValidatorException("Unable to find CustomizationID and ProfileID.");
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
