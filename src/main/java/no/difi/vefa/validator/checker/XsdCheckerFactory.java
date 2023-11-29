package no.difi.vefa.validator.checker;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.ArtifactHolder;
import no.difi.vefa.validator.util.HolderLSResolveResource;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

/**
 * @author erlend
 */
public class XsdCheckerFactory implements CheckerFactory {

    @Override
    public String[] types() {
        return new String[]{".xsd"};
    }

    @Override
    public Checker prepare(ArtifactHolder artifactHolder, String path) throws ValidatorException {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new HolderLSResolveResource(artifactHolder));

            return new XsdChecker(schemaFactory.newSchema(artifactHolder.getStream(path)));
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
