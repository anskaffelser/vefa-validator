package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.lang.ValidatorException;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
@Type("xml.ubl")
@MetaInfServices(Declaration.class)
public class UblDeclaration extends AbstractXmlDeclaration {

    private static final Pattern PATTERN = Pattern.compile("urn:oasis:names:specification:ubl:schema:xsd:(.+)-2::(.+)");

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return PATTERN.matcher(parent.get(0)).matches();
    }

    @Override
    public List<String> detect(byte[] content, List<String> parent) throws ValidatorException {
        List<String> results = new ArrayList<>();

        String customizationId = null;
        String profileId = null;

        try {
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(new ByteArrayInputStream(content));
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
                        results.add(String.format("%s#%s", profileId, customizationId));
                    }
                }
            }
        } catch (Exception e) {
            // No action.
        }

        if (customizationId != null)
            results.add(customizationId);

        if (results.size() > 0) {
            for (String identifier : new ArrayList<>(results))
                results.add(String.format("%s::%s", parent.get(0).split("::")[1], identifier));

            return results;
        }

        throw new ValidatorException("Unable to find CustomizationID and ProfileID.");
    }
}
