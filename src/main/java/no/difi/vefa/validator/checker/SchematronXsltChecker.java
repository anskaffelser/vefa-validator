package no.difi.vefa.validator.checker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.api.Section;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.JaxbUtils;
import no.difi.vefa.validator.util.SaxonUtils;
import no.difi.xsd.vefa.validator._1.SectionType;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class SchematronXsltChecker implements Checker {

    private static final JAXBContext JAXB_CONTEXT = JaxbUtils.context(SectionType.class);

    private final XsltExecutable xsltExecutable;

    @Inject
    @Named("schematron-svrl-parser")
    private Provider<XsltExecutable> parser;

    public SchematronXsltChecker(XsltExecutable xsltExecutable) {
        this.xsltExecutable = xsltExecutable;
    }

    @Override
    public void check(Document document, Section section) throws ValidatorException {
        long tsStart = System.currentTimeMillis();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            XsltTransformer parser = this.parser.get().load();
            XsltTransformer schematron = xsltExecutable.load();

            schematron.setErrorListener(SaxonUtils.ERROR_LISTENER);
            schematron.setMessageHandler(SaxonUtils.MESSAGE_HANDLER);
            schematron.setSource(new StreamSource(document.asInputStream()));
            schematron.setDestination(parser);

            parser.setErrorListener(SaxonUtils.ERROR_LISTENER);
            parser.setMessageHandler(SaxonUtils.MESSAGE_HANDLER);
            parser.setDestination(xsltExecutable.getProcessor().newSerializer(baos));

            schematron.transform();

            parser.close();
            schematron.close();

            long tsEnd = System.currentTimeMillis();

            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            SectionType sectionType = unmarshaller.unmarshal(new StreamSource(
                    new ByteArrayInputStream(baos.toByteArray())), SectionType.class).getValue();

            section.setTitle(sectionType.getTitle());
            section.add(sectionType.getAssertion());
            section.setRuntime((tsEnd - tsStart) + "ms");
        } catch (Exception e) {
            throw new ValidatorException(
                    String.format("Unable to perform check: %s", e.getMessage()), e);
        }
    }
}
