package no.difi.vefa.validator.util;

import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;

public class JAXBHelper {

    public static <T> T unmarshal(JAXBContext context, InputStream inputStream, Class<T> type) throws JAXBException {
        try {
            return context.createUnmarshaller().unmarshal(SecureXml.source(inputStream), type).getValue();
        } catch (ParserConfigurationException | SAXException e) {
            throw new JAXBException("Unable to securely parse XML.", e);
        }
    }

    public static JAXBContext context(Class... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load JAXBContext.", e);
        }
    }
}
