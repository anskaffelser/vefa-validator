package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Declaration;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
@Type("xml.uncefact")
@MetaInfServices(Declaration.class)
public class UnCefactDeclaration extends AbstractXmlDeclaration {

    private static List<String> informationElements = Arrays.asList(
            "BusinessProcessSpecifiedDocumentContextParameter",
            "GuidelineSpecifiedDocumentContextParameter");

    private static Pattern pattern = Pattern.compile("urn:un:unece:uncefact:data:standard:(.+)::(.+)");

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return pattern.matcher(parent.get(0)).matches();
    }

    @Override
    public List<String> detect(byte[] content, List<String> parent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parent.get(0).split("::")[1]);

        try {
            XMLEventReader xmlEventReader =
                    XML_INPUT_FACTORY.createXMLEventReader(new ByteArrayInputStream(content));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = (StartElement) xmlEvent;

                    if (informationElements.contains(startElement.getName().getLocalPart())) {
                        startElement = (StartElement) xmlEventReader.nextTag();

                        if ("ID".equals(startElement.getName().getLocalPart())) {
                            xmlEvent = xmlEventReader.nextEvent();

                            if (xmlEvent instanceof Characters) {
                                stringBuilder.append("::");
                                stringBuilder.append(((Characters) xmlEvent).getData());
                            }
                        }
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = (EndElement) xmlEvent;

                    if ("ExchangedDocumentContext".equals(endElement.getName().getLocalPart()))
                        return Collections.singletonList(stringBuilder.toString());
                }
            }
        } catch (Exception e) {
            // No action.
        }

        return null;
    }
}
