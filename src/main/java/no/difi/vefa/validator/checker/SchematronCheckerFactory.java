package no.difi.vefa.validator.checker;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.ArtifactHolder;
import no.difi.vefa.validator.util.SaxonUtils;

/**
 * Implementation performing step 3 (compilation) of Schematron.
 *
 * @author erlend
 */
@Type(".sch")
public class SchematronCheckerFactory implements CheckerFactory {

    @Inject
    @Named("schematron-step3")
    private Provider<XsltExecutable> schematronCompiler;

    @Inject
    private Provider<XsltCompiler> compilerProvider;

    @Inject
    private Injector injector;

    @Override
    public Checker prepare(ArtifactHolder artifactHolder, String path) throws ValidatorException {
        try {
            XdmDestination destination = new XdmDestination();

            XsltTransformer xsltTransformer = schematronCompiler.get().load();
            xsltTransformer.setErrorListener(SaxonUtils.ERROR_LISTENER);
            xsltTransformer.setMessageHandler(SaxonUtils.MESSAGE_HANDLER);
            xsltTransformer.setSource(artifactHolder.getStream(path));
            xsltTransformer.setDestination(destination);
            xsltTransformer.transform();

            XsltCompiler xsltCompiler = compilerProvider.get();
            xsltCompiler.setErrorListener(SaxonUtils.ERROR_LISTENER);

            Checker checker = new SchematronXsltChecker(xsltCompiler.compile(destination.getXdmNode().asSource()));
            injector.injectMembers(checker);
            return checker;
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
