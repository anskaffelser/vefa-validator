package no.difi.vefa.validator.api;

import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.xsd.vefa.validator._1.Report;

import java.io.OutputStream;
import java.util.List;

/**
 * Result of a validation.
 */
public interface Validation {

    /**
     * Render document to a stream.
     *
     * @param outputStream Stream to use.
     * @throws Exception
     */
    void render(OutputStream outputStream) throws Exception;

    /**
     * Render document to a stream, allows for extra configuration.
     *
     * @param outputStream Stream to use.
     * @param properties Extra configuration to use for this rendering.
     * @throws ValidatorException
     */
    void render(OutputStream outputStream, Properties properties) throws ValidatorException;

    /**
     * Returns true if validated document is renderable based upon same criteria as may be provide exception when using #render(...).
     *
     * @return 'true' if validated document is renderable.
     */
    boolean isRenderable();

    /**
     * Document used for validation as represented in the validator.
     *
     * @return Document object.
     */
    Document getDocument();

    /**
     * Report is the result of validation.
     *
     * @return Report
     */
    Report getReport();

    /**
     * Nested validations of validation.
     *
     * @return List of validations or null if none available.
     */
    List<Validation> getChildren();

}
