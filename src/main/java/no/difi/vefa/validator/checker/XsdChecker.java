package no.difi.vefa.validator.checker;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.api.Section;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.IOException;

public class XsdChecker implements Checker {

    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

    private final Schema schema;

    public XsdChecker(Schema schema) {
        this.schema = schema;
    }

    @Override
    public void check(Document document, Section section) {
        section.setTitle("XSD validation");

        Source xmlFile = new StreamSource(document.asInputStream());

        long tsStart = System.currentTimeMillis();
        try {
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
        } catch (SAXParseException e) {
            String humanMessage = e.getMessage();
            if (humanMessage.startsWith("cvc-complex-type.2.4.")) {
                try {
                    XMLStreamReader xmlStreamReader =
                            XML_INPUT_FACTORY.createXMLStreamReader(document.asInputStream());

                    // Go to root element.
                    while (xmlStreamReader.hasNext()
                            && xmlStreamReader.getEventType() != XMLStreamConstants.START_ELEMENT)
                        xmlStreamReader.next();

                    for (int i = 0; i < xmlStreamReader.getNamespaceCount(); i++) {
                        if (xmlStreamReader.getNamespacePrefix(i) == null)
                            humanMessage = humanMessage.replace(
                                    String.format("\"%s\":", xmlStreamReader.getNamespaceURI(i)), "");
                        else
                            humanMessage = humanMessage.replace(
                                    String.format("\"%s\"", xmlStreamReader.getNamespaceURI(i)),
                                    xmlStreamReader.getNamespacePrefix(i));
                    }

                    xmlStreamReader.close();
                } catch (XMLStreamException ex) {
                    // No action.
                }
            }

            if (humanMessage.startsWith("cvc-"))
                humanMessage = humanMessage.replaceAll("^(.*?): (.*)$", "$2");

            AssertionType assertionType = new AssertionType();
            assertionType.setIdentifier("XSD");
            assertionType.setText(e.getMessage());
            assertionType.setTextFriendly(humanMessage);
            assertionType.setLocation(String.format(
                    "Line %s, column %s.", e.getLineNumber(), e.getColumnNumber()));
            assertionType.setFlag(FlagType.FATAL);
            section.add(assertionType);
        } catch (SAXException | IOException e) {
            section.add("XSD", e.getMessage(), FlagType.FATAL);
        }

        section.setRuntime((System.currentTimeMillis() - tsStart) + "ms");
    }
}
