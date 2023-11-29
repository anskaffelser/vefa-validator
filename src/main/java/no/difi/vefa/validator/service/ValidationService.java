package no.difi.vefa.validator.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.difi.vefa.validator.model.Detected;
import no.difi.vefa.validator.model.Document;

import java.io.IOException;

@Singleton
public class ValidationService {

    @Inject
    private DetectorService detectorService;

    @Inject
    private ConfigurationService configurationService;

    public void validate(Document document) throws IOException {
        validate(document, detectorService.detect(document));
    }

    public void validate(Document document, Detected detected) {

    }
}
