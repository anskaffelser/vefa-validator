package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Declaration;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Type("xml.nobl")
@MetaInfServices(Declaration.class)
public class NoblDeclaration extends AbstractXmlDeclaration {

    private static final Pattern PATTERN = Pattern.compile("urn:fdc:difi.no:2018:nobl:(.+)-1::(.+)");

    private static final List<String> FIELDS = Arrays.asList("CustomizationID", "ProfileID");

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return PATTERN.matcher(parent.get(0)).matches();
    }

    @Override
    public List<String> detect(byte[] content, List<String> parent) {
        List<String> results = new ArrayList<>();

        String type = parent.get(0).split("::")[1];

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(type);

        try {
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(new ByteArrayInputStream(content));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = (StartElement) xmlEvent;

                    if (FIELDS.contains(startElement.getName().getLocalPart())) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent instanceof Characters) {
                            stringBuilder
                                    .append("::")
                                    .append(((Characters) xmlEvent).getData());

                            results.add(String.format("%s::%s", type, ((Characters) xmlEvent).getData()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // No action.
        }

        results.add(stringBuilder.toString());

        return results;
    }
}
