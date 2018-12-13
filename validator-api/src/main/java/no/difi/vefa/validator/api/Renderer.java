package no.difi.vefa.validator.api;

import no.difi.xsd.vefa.validator._1.StylesheetType;

import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Interface for classes creating presentation of business documents.
 *
 * The constructor must contain no parameters.
 */
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
