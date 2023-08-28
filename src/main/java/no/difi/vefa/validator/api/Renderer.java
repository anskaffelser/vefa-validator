package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;

import java.io.OutputStream;

/**
 * Interface for classes creating presentation of business documents.
 *
 * The constructor must contain no parameters.
 */
@Deprecated
public interface Renderer {

    /**
     * Writes presentation to a OutputStream given a business document.
     *
     * @param document Document to render.
     * @param properties Configuration for the presentation.
     * @param outputStream Stream to write presentation to.
     * @throws ValidatorException
     */
    void render(Document document, Properties properties, OutputStream outputStream) throws ValidatorException;

}
