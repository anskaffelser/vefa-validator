package no.difi.vefa.validator.checker;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.PathLSResolveResource;
import org.kohsuke.MetaInfServices;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author erlend
 */
@MetaInfServices
@Type(".xsd")
public class XsdCheckerFactory implements CheckerFactory {

    @Override
    public Checker prepare(Path path) throws ValidatorException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new PathLSResolveResource(path.getParent()));
            return new XsdChecker(schemaFactory.newSchema(new StreamSource(inputStream)));
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
