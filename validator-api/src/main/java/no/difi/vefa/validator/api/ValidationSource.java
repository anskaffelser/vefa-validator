package no.difi.vefa.validator.api;

import java.io.InputStream;

/**
 * Interface defining a representation of resources provided for an individual validation.
 */
public interface ValidationSource {

    /**
     * InputStream providing access to business document.
     *
     * @return Business document.
     */
    InputStream getInputStream();

    /**
     * Properties overriding existing properties may be provided.
     *
     * @return Overriding properties.
     */
    Properties getProperties();

}
