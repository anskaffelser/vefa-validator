package no.difi.vefa.validator.checker;

import net.sf.saxon.TransformerFactoryImpl;
import no.difi.vefa.validator.Document;
import no.difi.vefa.validator.Section;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerInfo;
import no.difi.vefa.validator.api.ValidatorException;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutput;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.nio.file.Files;
import java.nio.file.Path;

@CheckerInfo({".xsl", ".xslt"})
public class XsltChecker implements Checker {

    private static TransformerFactory transformerFactory = new TransformerFactoryImpl();

    private Transformer transformer;
    private JAXBResult jaxbResult;

    public void prepare(Path path) throws ValidatorException {
        try {
            transformer = transformerFactory.newTransformer(new StreamSource(Files.newInputStream(path)));
            jaxbResult = new JAXBResult(JAXBContext.newInstance(SchematronOutput.class));
        } catch (Exception e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public void check(Document document, Section section) throws ValidatorException {
        long tsStart = System.currentTimeMillis();
        try {
            transformer.transform(new StreamSource(document.getInputStream()), jaxbResult);
            long tsEnd = System.currentTimeMillis();

            SchematronOutput output = (SchematronOutput) jaxbResult.getResult();

            section.setTitle(output.getTitle());
            section.setRuntime((tsEnd - tsStart) + "ms");

            for (Object o : output.getActivePatternAndFiredRuleAndFailedAssert())
                if (o instanceof FailedAssert)
                    section.add((FailedAssert) o);
        } catch (Exception e) {
            throw new ValidatorException(
                    String.format("Unable to perform check: %s", e.getMessage()), e);
        }
    }
}
