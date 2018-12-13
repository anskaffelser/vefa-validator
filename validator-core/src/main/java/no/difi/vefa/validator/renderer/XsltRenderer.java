package no.difi.vefa.validator.renderer;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.util.PathURIResolver;
import no.difi.vefa.validator.util.SaxonHelper;
import no.difi.xsd.vefa.validator._1.SettingType;
import no.difi.xsd.vefa.validator._1.StylesheetType;

import javax.xml.transform.stream.StreamSource;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Defines presenter for templates defined by XSLT.
 */
public class XsltRenderer implements Renderer {

    private XsltExecutable xsltExecutable;

    /**
     * Holds the stylesheet definition.
     */
    private StylesheetType stylesheetType;

    private Path path;

    public XsltRenderer(XsltExecutable xsltExecutable, StylesheetType stylesheetType, Path path) {
        this.xsltExecutable = xsltExecutable;
        this.stylesheetType = stylesheetType;
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Document document, Properties properties, OutputStream outputStream) throws ValidatorException {
        try {
            XsltTransformer xsltTransformer = xsltExecutable.load();
            xsltTransformer.setURIResolver(new PathURIResolver(path.getParent()));

            // Look through default values for stylesheet.
            for (SettingType setting : stylesheetType.getSetting()) {
                xsltTransformer.setParameter(
                        new QName(setting.getName()),
                        new XdmAtomicValue(properties.getString(String.format("stylesheet.%s.%s", stylesheetType.getIdentifier(), setting.getName()), setting.getDefaultValue()))
                );
            }

            // Use transformer to write the result to stream.
            xsltTransformer.setSource(new StreamSource(document.getInputStream()));
            xsltTransformer.setDestination(SaxonHelper.PROCESSOR.newSerializer(outputStream));
            xsltTransformer.transform();
            xsltTransformer.close();
        } catch (Exception e) {
            throw new ValidatorException("Unable to render document.", e);
        }
    }
}
