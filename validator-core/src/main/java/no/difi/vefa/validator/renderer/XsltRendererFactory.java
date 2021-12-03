package no.difi.vefa.validator.renderer;

import com.google.inject.Inject;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.ArtifactHolder;
import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.api.RendererFactory;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.HolderURIResolver;
import no.difi.vefa.validator.util.SaxonErrorListener;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.kohsuke.MetaInfServices;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * @author erlend
 */
@Deprecated
@MetaInfServices
@Type({".xsl", ".xslt"})
public class XsltRendererFactory implements RendererFactory {

    @Inject
    private Processor processor;

    @Override
    public Renderer prepare(StylesheetType stylesheetType, ArtifactHolder artifactHolder, String path) throws ValidatorException {
        try (InputStream inputStream = artifactHolder.getInputStream(path)) {
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            xsltCompiler.setErrorListener(SaxonErrorListener.INSTANCE);
            xsltCompiler.setURIResolver(new HolderURIResolver(artifactHolder, path));
            return new XsltRenderer(xsltCompiler.compile(new StreamSource(inputStream)),
                    stylesheetType, artifactHolder, path, processor);
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
