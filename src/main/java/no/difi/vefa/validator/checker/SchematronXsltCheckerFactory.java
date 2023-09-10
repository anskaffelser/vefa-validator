package no.difi.vefa.validator.checker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.ArtifactHolder;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.SaxonUtils;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * @author erlend
 */
@Type({".xsl", ".xslt", ".svrl.xsl", ".svrl.xslt", ".sch.xslt"})
public class SchematronXsltCheckerFactory implements CheckerFactory {

    @Inject
    private Processor processor;

    @Inject
    private Injector injector;

    @Override
    public Checker prepare(ArtifactHolder artifactHolder, String path) throws ValidatorException {
        try (InputStream inputStream = artifactHolder.getInputStream(path)) {
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            xsltCompiler.setErrorListener(SaxonUtils.ERROR_LISTENER);

            Checker checker = new SchematronXsltChecker(processor, xsltCompiler.compile(new StreamSource(inputStream)));
            injector.injectMembers(checker);
            return checker;

        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
