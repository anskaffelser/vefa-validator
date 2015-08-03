package no.difi.vefa.validator.presenter;

import net.sf.saxon.TransformerFactoryImpl;
import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.ValidatorException;
import no.difi.vefa.validator.api.Presenter;
import no.difi.vefa.validator.api.PresenterInfo;
import no.difi.vefa.validator.util.PathURIResolver;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@PresenterInfo({".xsl", ".xslt"})
public class XsltPresenter implements Presenter {

    private Transformer transformer;

    public XsltPresenter(Path path) throws ValidatorException {
        try {
            TransformerFactory transformerFactory = new TransformerFactoryImpl();
            transformerFactory.setURIResolver(new PathURIResolver(path.getParent()));
            transformer = transformerFactory.newTransformer(new StreamSource(Files.newInputStream(path)));
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public void present(Document document, OutputStream outputStream) throws ValidatorException {
        try {
            transformer.transform(new StreamSource(document.getInputStream()), new StreamResult(outputStream));
        } catch (TransformerException e) {
            throw new ValidatorException("Unable to render document.", e);
        }
    }
}
