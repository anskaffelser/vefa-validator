package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.DeclarationWithConverter;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.ValidatorTestExpectation;
import no.difi.xsd.vefa.validator._1.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ValidatorTestDeclaration extends SimpleXmlDeclaration implements DeclarationWithConverter {

    private static Logger logger = LoggerFactory.getLogger(ValidatorTestDeclaration.class);

    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Test.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ValidatorTestDeclaration() {
        super("http://difi.no/xsd/vefa/validator/1.0", "test");
    }

    @Override
    public String detect(byte[] content) throws ValidatorException {
        try {
            XMLStreamReader source = xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(content));

            do {
                if (source.getEventType() == XMLStreamConstants.START_ELEMENT && source.getNamespaceURI().equals(namespace))
                    for (int i = 0; i < source.getAttributeCount(); i++)
                        if (source.getAttributeName(i).toString().equals("configuration"))
                            return String.format("configuration::%s", source.getAttributeValue(i));
            } while (source.hasNext() && source.next() > 0);
        } catch (XMLStreamException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new ValidatorTestExpectation(content);
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException {
        try {
            Test test = convertInputStream(inputStream);

            if (test.getAny() instanceof Node) {
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource((Node) test.getAny()), new StreamResult(outputStream));
            }
        } catch (JAXBException | TransformerException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private Test convertInputStream(InputStream inputStream) throws JAXBException {
        return jaxbContext.createUnmarshaller().unmarshal(new StreamSource(inputStream), Test.class).getValue();
    }
}
