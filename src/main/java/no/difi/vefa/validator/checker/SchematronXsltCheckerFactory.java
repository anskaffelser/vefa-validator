package no.difi.vefa.validator.checker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import net.sf.saxon.s9api.XsltCompiler;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.ArtifactHolder;
import no.difi.vefa.validator.util.SaxonUtils;

/**
 * @author erlend
 */
@Type({".xsl", ".xslt", ".svrl.xsl", ".svrl.xslt", ".sch.xslt"})
public class SchematronXsltCheckerFactory implements CheckerFactory {

    @Inject
    private Provider<XsltCompiler> compilerProvider;

    @Inject
    private Injector injector;

    @Override
    public Checker prepare(ArtifactHolder artifactHolder, String path) throws ValidatorException {
        try {
            XsltCompiler xsltCompiler = compilerProvider.get();
            xsltCompiler.setErrorListener(SaxonUtils.ERROR_LISTENER);

            Checker checker = new SchematronXsltChecker(xsltCompiler.compile(artifactHolder.getStream(path)));
            injector.injectMembers(checker);
            return checker;
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
