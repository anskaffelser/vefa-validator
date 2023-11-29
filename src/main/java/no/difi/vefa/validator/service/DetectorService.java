package no.difi.vefa.validator.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.XsltExecutable;
import no.difi.vefa.validator.lang.TransformationException;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.Detected;
import no.difi.vefa.validator.model.Document;

import java.io.IOException;

@Slf4j
@Singleton
public class DetectorService {

    @Inject
    @Named("detector")
    private XsltExecutable detector;

    public Detected detect(Document document) throws IOException {
        try {
            return Detected.of(document.transform(detector).asInputStream());
        } catch (TransformationException e) {
            return Detected.UNKNOWN;
        } catch (ValidatorException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
