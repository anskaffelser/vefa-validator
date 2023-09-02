package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltExecutable;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class DetectorModule extends AbstractModule {

    @Provides
    @Singleton
    @Named("detector")
    public XsltExecutable getDetectorXslt(Processor processor) throws IOException, SaxonApiException {
        var compiler = processor.newXsltCompiler();

        try (var inputStream = getClass().getResourceAsStream("/vefa-validator/xslt/detector.xslt")) {
            return compiler.compile(new StreamSource(inputStream));
        }
    }
}
