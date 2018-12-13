package no.difi.vefa.validator.renderer;

import com.google.inject.Inject;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;
import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.api.RendererFactory;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.PathURIResolver;
import no.difi.vefa.validator.util.SaxonErrorListener;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.kohsuke.MetaInfServices;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author erlend
 */
@MetaInfServices
@Type({".xsl", ".xslt"})
public class XsltRendererFactory implements RendererFactory {

    @Inject
    private Processor processor;

    @Override
    public Renderer prepare(StylesheetType stylesheetType, Path path) throws ValidatorException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            XsltCompiler xsltCompiler = processor.newXsltCompiler();
            xsltCompiler.setErrorListener(SaxonErrorListener.INSTANCE);
            xsltCompiler.setURIResolver(new PathURIResolver(path.getParent()));
            return new XsltRenderer(xsltCompiler.compile(new StreamSource(inputStream)),
                    stylesheetType, path, processor);
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
