package no.difi.vefa.validator.checker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import net.sf.saxon.s9api.*;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.SaxonErrorListener;
import org.kohsuke.MetaInfServices;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation performing step 3 (compilation) of Schematron.
 *
 * @author erlend
 */
@MetaInfServices
@Type(".sch")
public class SchematronCheckerFactory implements CheckerFactory {

    @Inject
    @Named("schematron-step3")
    private Provider<XsltExecutable> schematronCompiler;

    @Inject
    private Processor processor;

    @Inject
    private Injector injector;

    @Override
    public Checker prepare(Path path) throws ValidatorException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            XdmDestination destination = new XdmDestination();

            XsltTransformer xsltTransformer = schematronCompiler.get().load();
            xsltTransformer.setErrorListener(SaxonErrorListener.INSTANCE);
            xsltTransformer.setSource(new StreamSource(inputStream));
            xsltTransformer.setDestination(destination);
            xsltTransformer.transform();

            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            xsltCompiler.setErrorListener(SaxonErrorListener.INSTANCE);

            Checker checker = new SchematronXsltChecker(processor, xsltCompiler.compile(destination.getXdmNode().asSource()));
            injector.injectMembers(checker);
            return checker;
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
