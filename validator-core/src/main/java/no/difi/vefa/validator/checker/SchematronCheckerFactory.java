package no.difi.vefa.validator.checker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.api.CheckerInfo;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.util.SaxonErrorListener;
import no.difi.vefa.validator.util.SaxonHelper;
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
@CheckerInfo(".sch")
public class SchematronCheckerFactory implements CheckerFactory {

    @Inject
    @Named("schematron-step3")
    private Provider<XsltExecutable> schematronCompiler;

    @Override
    public Checker prepare(Path path) throws ValidatorException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            XdmDestination destination = new XdmDestination();

            XsltTransformer xsltTransformer = schematronCompiler.get().load();
            xsltTransformer.setErrorListener(SaxonErrorListener.INSTANCE);
            xsltTransformer.setSource(new StreamSource(inputStream));
            xsltTransformer.setDestination(destination);
            xsltTransformer.transform();

            XsltCompiler xsltCompiler = SaxonHelper.newXsltCompiler();
            xsltCompiler.setErrorListener(SaxonErrorListener.INSTANCE);
            return new SchematronXsltChecker(xsltCompiler.compile(destination.getXdmNode().asSource()));
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
