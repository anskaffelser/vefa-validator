package no.difi.vefa.validator.api;

import no.difi.vefa.validator.model.Document;
import no.difi.xsd.vefa.validator._1.Report;

import java.util.List;

/**
 * Result of a validation.
 */
public interface Validation {

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
