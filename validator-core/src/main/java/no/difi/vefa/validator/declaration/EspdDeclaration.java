package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.StreamUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Type("xml.espd")
public class EspdDeclaration extends AbstractXmlDeclaration {

    private final static List<String> validParents = Arrays.asList(
            "urn:grow:names:specification:ubl:schema:xsd:ESPDRequest-1::ESPDRequest",
            "urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse"
    );

    @Override
    public boolean verify(byte[] content, List<String> parent) throws ValidatorException {
        return validParents.contains(parent.get(0));
    }

    @Override
    public List<String> detect(InputStream contentStream, List<String> parent) throws ValidatorException {
        List<String> results = new ArrayList<>();

        try {
            byte[] content= StreamUtils.readAndReset(contentStream, 10*1024);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(new ByteArrayInputStream(content));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    if ("CustomizationID".equals(((StartElement) xmlEvent).getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            results.add(String.format("%s::%s", parent.get(0), ((Characters) xmlEvent).getData()));
                    }
                    if ("VersionID".equals(((StartElement) xmlEvent).getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters)
                            results.add(String.format("%s::%s", parent.get(0), ((Characters) xmlEvent).getData()));
                    }
                }
            }
        } catch (Exception e) {
            // No action.
        }

        return results.isEmpty() ? parent : results;
    }
}
