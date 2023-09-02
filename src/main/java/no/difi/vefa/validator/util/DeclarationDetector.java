package no.difi.vefa.validator.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltExecutable;
import no.difi.vefa.validator.api.Document;
import no.difi.vefa.validator.lang.ValidatorException;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Singleton
public class DeclarationDetector {

    @Inject
    @Named("detector")
    private XsltExecutable detector;

    public DeclarationIdentification detect(Document document) throws IOException {
        try {
            var baos = new ByteArrayOutputStream();

            var transformer = detector.load();
            transformer.setSource(new StreamSource(document.asInputStream()));
            transformer.setDestination(detector.getProcessor().newSerializer(baos));
            transformer.transform();

            System.out.println(baos);

            return DeclarationIdentification.of(baos);
        } catch (SaxonApiException e) {
            return DeclarationIdentification.UNKNOWN;
        } catch (ValidatorException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
