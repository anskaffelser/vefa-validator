package no.difi.vefa.validator.util;

import javax.xml.bind.JAXBContext;

public class JAXBHelper {

    public static JAXBContext context(Class... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load JAXBContext.", e);
        }
    }
}
