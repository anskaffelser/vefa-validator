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

    private static final String NAMESPACE = "urn:grow:names:specification:ubl:schema:xsd:ESPD";

    @Override
    public boolean verify(byte[] content) throws ValidatorException {
        String namespace = XmlUtils.extractRootNamespace(new String(content));
        return namespace != null && namespace.startsWith(NAMESPACE);
    }

    @Override
    public String detect(byte[] content) throws ValidatorException {
        String rootName = null;
        String customizationId = null;
        String profileId = null;
        String versionId = null;

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(content));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = (StartElement) xmlEvent;
                    String localName = startElement.getName().getLocalPart();

                    if ("ESPDResponse".equals(localName) || "ESPDRequest".equals(localName))
                        rootName = localName;
                    else if ("CustomizationID".equals(localName)) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            customizationId = ((Characters) xmlEvent).getData();
                    } else if ("ProfileID".equals(localName)) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            profileId = ((Characters) xmlEvent).getData();

                        // ProfileID is the last in sequence.
                        return String.format("%s#%s", profileId, customizationId);
                    } else if ("VersionID".equals(localName)) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            versionId = ((Characters) xmlEvent).getData();

                        return String.format("ESPD::%s::%s", rootName, versionId);
                    }

                }
            }
        } catch (Exception e) {
            // No action.
        }

        if (rootName != null)
            return String.format("ESPD::%s", rootName);

        throw new ValidatorException("ESPD not recognized.");
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
