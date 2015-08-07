package no.difi.vefa.validator.api;

import no.difi.vefa.validator.Document;
import no.difi.xsd.vefa.validator._1.StylesheetType;

import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Interface for classes creating presentation of business documents.
 *
 * The constructor must contain no parameters.
 */
public interface Presenter {

    /**
     * Method for preparing for use, can be seen as a constructor.
     *
     * @param stylesheetType Definition of the stylesheet defining the presenter.
     * @param path Path of file used for presentation.
     * @throws ValidatorException
     */
    void prepare(StylesheetType stylesheetType, Path path) throws ValidatorException;

    /**
     * Writes presentation to a OutputStream given a business document.
     *
     * @param document Document to present.
     * @param properties Configuration for the presentation.
     * @param outputStream Stream to write presentation to.
     * @throws ValidatorException
     */
    void present(Document document, Properties properties, OutputStream outputStream) throws ValidatorException;

}
