package no.difi.vefa.validator.util;

import jakarta.xml.bind.JAXBContext;

public interface JaxbUtils {

    static JAXBContext context(Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load JAXBContext.", e);
        }
    }
}
