package no.difi.vefa.validator.checker;

import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.Section;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerInfo;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.util.PathLSResolveResource;
import no.difi.xsd.vefa.validator._1.FlagType;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.nio.file.Files;
import java.nio.file.Path;

@CheckerInfo({".xsd"})
public class XsdChecker implements Checker {

    private Validator validator;

    public void prepare(Path path) throws ValidatorException {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new PathLSResolveResource(path.getParent()));
            Schema schema = schemaFactory.newSchema(new StreamSource(Files.newInputStream(path)));
            validator = schema.newValidator();
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public void check(Document document, Section section) {
        section.setTitle("XSD validation");

        Source xmlFile = new StreamSource(document.getInputStream());

        long tsStart = System.currentTimeMillis();
        try {
            validator.validate(xmlFile);
        } catch (Exception e) {
            section.add("XSD", e.getLocalizedMessage(), FlagType.FATAL);
        }

        section.setRuntime((System.currentTimeMillis() - tsStart) + "ms");
    }
}
