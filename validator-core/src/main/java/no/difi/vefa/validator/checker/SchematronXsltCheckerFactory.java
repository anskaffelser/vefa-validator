package no.difi.vefa.validator.checker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;
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
 * @author erlend
 */
@MetaInfServices
@Type({".xsl", ".xslt", ".svrl.xsl", ".svrl.xslt", ".sch.xslt"})
public class SchematronXsltCheckerFactory implements CheckerFactory {

    @Inject
    private Processor processor;

    @Inject
    private Injector injector;

    @Override
    public Checker prepare(Path path) throws ValidatorException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            xsltCompiler.setErrorListener(SaxonErrorListener.INSTANCE);

            Checker checker = new SchematronXsltChecker(processor, xsltCompiler.compile(new StreamSource(inputStream)));
            injector.injectMembers(checker);
            return checker;

        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
