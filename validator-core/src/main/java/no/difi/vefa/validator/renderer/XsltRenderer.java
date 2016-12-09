package no.difi.vefa.validator.renderer;

import net.sf.saxon.s9api.*;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.util.PathURIResolver;
import no.difi.vefa.validator.util.SaxonErrorListener;
import no.difi.xsd.vefa.validator._1.SettingType;
import no.difi.xsd.vefa.validator._1.StylesheetType;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Defines presenter for templates defined by XSLT.
 */
@RendererInfo({".xsl", ".xslt"})
public class XsltRenderer implements Renderer {

    // private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

    private static DocumentBuilderFactory documentBuilderFactory;
    private static TransformerFactory transformerFactory;

    private XsltExecutable xsltExecutable;

    /**
     * Holds the stylesheet definition.
     */
    private StylesheetType stylesheetType;

    private Path path;

    static {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // documentBuilderFactory.setNamespaceAware(true);

        transformerFactory = TransformerFactory.newInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare(StylesheetType stylesheetType, Path path) throws ValidatorException {
        // Keep reference.
        this.stylesheetType = stylesheetType;
        this.path = path;

        try {
            XsltCompiler xsltCompiler = new Processor(false).newXsltCompiler();
            xsltCompiler.setErrorListener(SaxonErrorListener.INSTANCE);
            xsltCompiler.setURIResolver(new PathURIResolver(path.getParent()));
            xsltExecutable = xsltCompiler.compile(new StreamSource(Files.newInputStream(path)));
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
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
            for (SettingType setting : stylesheetType.getSetting())
                xsltTransformer.setParameter(
                        new QName(setting.getName()),
                        new XdmAtomicValue(properties.getString(String.format("stylesheet.%s.%s", stylesheetType.getIdentifier(), setting.getName()), setting.getDefaultValue()))
                );

            org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().newDocument();

            // Use transformer to write the result to stream.
            xsltTransformer.setSource(new StreamSource(document.getInputStream()));
            // xsltTransformer.setDestination(new XMLStreamWriterDestination(xmlOutputFactory.createXMLStreamWriter(outputStream, "UTF-8")));
            xsltTransformer.setDestination(new DOMDestination(doc));
            xsltTransformer.transform();
            xsltTransformer.close();

            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
        } catch (Exception e) {
            throw new ValidatorException("Unable to render document.", e);
        }
    }
}
