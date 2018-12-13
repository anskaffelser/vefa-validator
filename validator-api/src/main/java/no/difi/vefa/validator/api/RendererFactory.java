package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.xsd.vefa.validator._1.StylesheetType;

import java.nio.file.Path;

/**
 * @author erlend
 */
public interface RendererFactory {

    /**
     * Method for preparing for use, can be seen as a constructor.
     *
     * @param stylesheetType Definition of the stylesheet defining the presenter.
     * @param path Path of file used for presentation.
     * @throws ValidatorException
     */
    Renderer prepare(StylesheetType stylesheetType, Path path) throws ValidatorException;

}
