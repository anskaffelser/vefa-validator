package no.difi.vefa.validator.declaration;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.DeclarationWithConverter;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.expectation.ValidatorTestExpectation;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.vefa.validator.util.StreamUtils;
import no.difi.xsd.vefa.validator._1.Test;
import org.w3c.dom.Node;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Type("xml.test")
public class ValidatorTestDeclaration extends SimpleXmlDeclaration implements DeclarationWithConverter {

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    private static final JAXBContext JAXB_CONTEXT = JAXBHelper.context(Test.class);

    public ValidatorTestDeclaration() {
        super("http://difi.no/xsd/vefa/validator/1.0", "test");
    }

    @Override
    public List<String> detect( InputStream contentStream, List<String> parent) throws ValidatorException {
        try {
            byte[] content= StreamUtils.readAndReset(contentStream, 10*1024);
            XMLStreamReader source = XML_INPUT_FACTORY.createXMLStreamReader(new ByteArrayInputStream(content));
            do {
                if (source.getEventType() == XMLStreamConstants.START_ELEMENT
                        && source.getNamespaceURI().equals(namespace))
                    for (int i = 0; i < source.getAttributeCount(); i++)
                        if (source.getAttributeName(i).toString().equals("configuration"))
                            return Collections.singletonList(String.format(
                                    "configuration::%s", source.getAttributeValue(i)));
            } while (source.hasNext() && source.next() > 0);
        } catch (IOException |XMLStreamException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Expectation expectations(byte[] content) {
        return new ValidatorTestExpectation(content);
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) {
        try {
            Test test = convertInputStream(inputStream);

            if (test.getAny() instanceof Node) {
                Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource((Node) test.getAny()), new StreamResult(outputStream));
            }
        } catch (JAXBException | TransformerException e) {
            log.warn(e.getMessage(), e);
        }
    }

    private Test convertInputStream(InputStream inputStream) throws JAXBException {
        return JAXB_CONTEXT.createUnmarshaller().unmarshal(new StreamSource(inputStream), Test.class).getValue();
    }
}
