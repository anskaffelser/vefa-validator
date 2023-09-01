package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.util.StreamUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
@Type("xml.uncefact")
public class UnCefactDeclaration extends AbstractXmlDeclaration {

    private final static List<String> informationElements = Arrays.asList(
            "BusinessProcessSpecifiedDocumentContextParameter",
            "GuidelineSpecifiedDocumentContextParameter");

    private static final Pattern pattern = Pattern.compile("urn:un:unece:uncefact:data:standard:(.+)::(.+)");

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return pattern.matcher(parent.get(0)).matches();
    }

    @Override
    public List<String> detect(InputStream contentStream, List<String> parent) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parent.get(0).split("::")[1]);

        try {
            byte[] content= StreamUtils.readAndReset(contentStream, 10*1024);
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
