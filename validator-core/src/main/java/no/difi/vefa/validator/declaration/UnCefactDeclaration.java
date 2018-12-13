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
@Type("xml.uncefact")
@MetaInfServices(Declaration.class)
public class UnCefactDeclaration extends AbstractXmlDeclaration {

    private static Pattern pattern = Pattern.compile("urn:un:unece:uncefact:data:standard:(.+)::(.+)");

    public boolean verify(byte[] content, String parent) throws ValidatorException {
        return pattern.matcher(parent).matches();
    }

    public String detect(byte[] content, String parent) throws ValidatorException {
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(content));
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = (StartElement) xmlEvent;

                    if ("GuidelineSpecifiedDocumentContextParameter".equals(startElement.getName().getLocalPart())) {
                        startElement = (StartElement) xmlEventReader.nextTag();

                        if ("ID".equals(startElement.getName().getLocalPart())) {
                            xmlEvent = xmlEventReader.nextEvent();

                            if (xmlEvent instanceof Characters)
                                return String.format("%s::%s", parent.split("::")[1], ((Characters) xmlEvent).getData());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // No action.
        }

        return null;
    }
}
