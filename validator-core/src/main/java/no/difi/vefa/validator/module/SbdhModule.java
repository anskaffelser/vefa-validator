package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import org.kohsuke.MetaInfServices;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author erlend
 */
@Slf4j
@MetaInfServices(Module.class)
public class SbdhModule extends AbstractModule {

    @Provides
    @Named("sbdh-extractor")
    @Singleton
    public XsltExecutable getSchematronCompiler(Processor processor) {
        try (InputStream inputStream = getClass().getResourceAsStream("/vefa-validator/xslt/sbdh-extractor.xslt")) {
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            return xsltCompiler.compile(new StreamSource(inputStream));
        } catch (IOException | SaxonApiException e) {
            throw new IllegalStateException("Unable to load extraction of SBDH content.");
        }
    }
}
