package no.difi.vefa.validator.renderer;

import net.sf.saxon.s9api.*;
import no.difi.vefa.validator.api.ArtifactHolder;
import no.difi.vefa.validator.api.Document;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.HolderURIResolver;
import no.difi.xsd.vefa.validator._1.SettingType;
import no.difi.xsd.vefa.validator._1.StylesheetType;

import javax.xml.transform.stream.StreamSource;
import java.io.OutputStream;

/**
 * Defines presenter for templates defined by XSLT.
 */
@Deprecated
public class XsltRenderer implements Renderer {

    private XsltExecutable xsltExecutable;

    /**
     * Holds the stylesheet definition.
     */
    private StylesheetType stylesheetType;

    private ArtifactHolder artifactHolder;

    private String path;

    private Processor processor;

    public XsltRenderer(XsltExecutable xsltExecutable, StylesheetType stylesheetType, ArtifactHolder artifactHolder, String path, Processor processor) {
        this.xsltExecutable = xsltExecutable;
        this.artifactHolder = artifactHolder;
        this.stylesheetType = stylesheetType;
        this.path = path;
        this.processor = processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Document document, Properties properties, OutputStream outputStream) throws ValidatorException {
        try {
            XsltTransformer xsltTransformer = xsltExecutable.load();
            xsltTransformer.setURIResolver(new HolderURIResolver(artifactHolder, path));

            // Look through default values for stylesheet.
            for (SettingType setting : stylesheetType.getSetting())
                setParameter(
                        xsltTransformer,
                        setting.getName(),
                        properties.getString(String.format("stylesheet.%s.%s",
                                stylesheetType.getIdentifier(), setting.getName()), setting.getDefaultValue()));

            // Use transformer to write the result to stream.
            xsltTransformer.setSource(new StreamSource(document.getInputStream()));
            xsltTransformer.setDestination(processor.newSerializer(outputStream));
            xsltTransformer.transform();
            xsltTransformer.close();
        } catch (Exception e) {
            throw new ValidatorException("Unable to render document.", e);
        }
    }

    private static void setParameter(XsltTransformer transformer, String key, String value) {
        transformer.setParameter(new QName(key), new XdmAtomicValue(value));
    }
}
