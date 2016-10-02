package no.difi.vefa.validator.checker;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.util.PathLSResolveResource;
import no.difi.xsd.vefa.validator._1.FlagType;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.nio.file.Files;
import java.nio.file.Path;

@CheckerInfo({".xsd"})
public class XsdChecker implements Checker {

    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

    private Validator validator;

    public void prepare(Path path) throws ValidatorException {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new PathLSResolveResource(path.getParent()));
            Schema schema = schemaFactory.newSchema(new StreamSource(Files.newInputStream(path)));
            validator = schema.newValidator();
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public void check(Document document, Section section) {
        section.setTitle("XSD validation");

        Source xmlFile = new StreamSource(document.getInputStream());

        long tsStart = System.currentTimeMillis();
        try {
            validator.validate(xmlFile);
        } catch (Exception e) {
            String humanMessage = null;
            if (e.getMessage().contains("Invalid content was found starting with element")) {
                try {
                    XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(document.getInputStream());

                    // Go to root element.
                    while (xmlStreamReader.hasNext() && xmlStreamReader.getEventType() != XMLStreamConstants.START_ELEMENT)
                        xmlStreamReader.next();

                    humanMessage = e.getMessage().replace("cvc-complex-type.2.4.a: ", "");
                    for (int i = 0; i < xmlStreamReader.getNamespaceCount(); i++) {
                        if (xmlStreamReader.getNamespacePrefix(i) == null)
                            humanMessage = humanMessage.replace(String.format("\"%s\":", xmlStreamReader.getNamespaceURI(i)), "");
                        else
                            humanMessage = humanMessage.replace(String.format("\"%s\"", xmlStreamReader.getNamespaceURI(i)), xmlStreamReader.getNamespacePrefix(i));
                    }
                } catch (XMLStreamException ex) {
                    // No action.
                }
            }

            section.add("XSD", e.getMessage(), humanMessage, FlagType.FATAL);
        }

        section.setRuntime((System.currentTimeMillis() - tsStart) + "ms");
    }
}
