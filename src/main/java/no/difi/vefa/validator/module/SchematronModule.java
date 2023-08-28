package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import no.difi.vefa.validator.util.ClasspathURIResolver;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author erlend
 */
@Slf4j
public class SchematronModule extends AbstractModule {

    @Provides
    @Named("schematron-step3")
    @Singleton
    public XsltExecutable getSchematronCompiler(Processor processor) {
        try (InputStream inputStream = getClass().getResourceAsStream("/iso-schematron-xslt2/iso_svrl_for_xslt2.xsl")) {
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            xsltCompiler.setURIResolver(new ClasspathURIResolver("/iso-schematron-xslt2"));
            return xsltCompiler.compile(new StreamSource(inputStream));
        } catch (IOException | SaxonApiException e) {
            throw new IllegalStateException("Unable to load parsing of Schematron.");
        }
    }

    @Provides
    @Named("schematron-svrl-parser")
    @Singleton
    public XsltExecutable getSchematronSvrlParser(Processor processor) {
        try (InputStream inputStream = getClass().getResourceAsStream("/vefa-validator/xslt/svrl-parser.xslt")) {
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            return xsltCompiler.compile(new StreamSource(inputStream));
        } catch (IOException | SaxonApiException e) {
            throw new IllegalStateException("Unable to load parsing of Schematron reports.");
        }
    }
}
