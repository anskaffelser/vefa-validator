package no.difi.vefa.validator.presenter;

import net.sf.saxon.TransformerFactoryImpl;
import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.ValidatorException;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Presenter;
import no.difi.vefa.validator.api.PresenterInfo;
import no.difi.vefa.validator.util.PathURIResolver;
import no.difi.xsd.vefa.validator._1.SettingType;
import no.difi.xsd.vefa.validator._1.StylesheetType;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Defines presenter for templates defined by XSLT.
 */
@PresenterInfo({".xsl", ".xslt"})
public class XsltPresenter implements Presenter {

    /**
     * Holds the transformer ready for use.
     */
    private Transformer transformer;

    /**
     * Holds the stylesheet definition.
     */
    private StylesheetType stylesheetType;

    /**
     * @inheritDoc
     */
    @Override
    public void prepare(StylesheetType stylesheetType, Path path) throws ValidatorException {
        // Keep reference.
        this.stylesheetType = stylesheetType;

        try {
            // Create new factory to create transformer
            TransformerFactory transformerFactory = new TransformerFactoryImpl();
            // Define an use a URIResolver supporting Path.
            transformerFactory.setURIResolver(new PathURIResolver(path.getParent()));
            // Create the transformer using the template file.
            transformer = transformerFactory.newTransformer(new StreamSource(Files.newInputStream(path)));
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void present(Document document, Properties properties, OutputStream outputStream) throws ValidatorException {
        try {
            // Look through default values for stylesheet.
            for (SettingType setting : stylesheetType.getSetting())
                transformer.setParameter(setting.getName(), properties.get(String.format("stylesheet.%s.%s", stylesheetType.getIdentifier(), setting.getName()), setting.getDefaultValue()));

            // Use transformer to write the result to stream.
            transformer.transform(new StreamSource(document.getInputStream()), new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new ValidatorException("Unable to render document.", e);
        }
    }
}
