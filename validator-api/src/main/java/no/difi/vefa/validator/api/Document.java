package no.difi.vefa.validator.api;

import java.io.ByteArrayInputStream;

public interface Document {

    /**
     * Declaration detected in document for validation.
     *
     * @return Declaration
     */
    Declaration getDeclaration();

    /**
     * Helper returning validated document as ByteArrayInputStream ready for use.
     *
     * @return Validated document
     */
    ByteArrayInputStream getInputStream();
}
