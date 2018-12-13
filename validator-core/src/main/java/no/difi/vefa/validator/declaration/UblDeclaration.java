package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Type;
import no.difi.vefa.validator.api.ValidatorException;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
@Type("xml.ubl")
@MetaInfServices(Declaration.class)
public class UblDeclaration extends AbstractXmlDeclaration {

    private static final Pattern PATTERN = Pattern.compile("urn:oasis:names:specification:ubl:schema:xsd:(.+)-2::(.+)");

    private static final String TC434 = "urn:cen.eu:en16931:2017";

    public boolean verify(byte[] content, String parent) throws ValidatorException {
        return PATTERN.matcher(parent).matches();
    }

    public String detect(byte[] content, String parent) throws ValidatorException {
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

                        if (customizationId != null && customizationId.startsWith(TC434))
                            customizationId = String.format("%s::%s", parent.split("::")[1], customizationId);
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

        if (customizationId != null)
            return customizationId;

        throw new ValidatorException("Unable to find CustomizationID and ProfileID.");
    }
}
