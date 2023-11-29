package no.difi.vefa.validator.service;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import jakarta.xml.bind.JAXBContext;
import net.sf.saxon.s9api.XsltExecutable;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.Section;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.expectation.ValidatorTestExpectation;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.util.JaxbUtils;
import no.difi.xsd.vefa.validator._1.Test;
import no.difi.xsd.vefa.validator._1.TestSet;
import org.w3c.dom.Node;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class TestingService {

    private static final JAXBContext JAXB = JaxbUtils.context(TestSet.class);

    @Inject
    @Named("test-preparer")
    private Provider<XsltExecutable> preparerProvider;

    @Inject
    private Validator validator;

    @Inject
    private DetectorService detectorService;

    public List<Validation> verify(Path path) throws IOException, ValidatorException {
        return verify(Document.of(path));
    }

    public List<Validation> verify(Document document) throws IOException, ValidatorException {
        var detected = detectorService.detect(document);

        if ("http://difi.no/xsd/vefa/validator/1.0".equals(detected.getProperties().get("xml.namespace"))) {
            if ("testSet".equals(detected.getProperties().get("xml.element"))) {
                return handleTestSet(document);
            } else if ("test".equals(detected.getProperties().get("xml.element"))) {
                return List.of(handleTest(document));
            }
        } else {
            return List.of(handleDocument(document));
        }

        throw new ValidatorException("Unable to use provided document as test.");
    }

    private List<Validation> handleTestSet(Document document) throws ValidatorException {
        var tests = document
                .transform(preparerProvider.get())
                .unmarshal(JAXB, TestSet.class)
                .getTest();

        var validations = new ArrayList<Validation>();
        for (var test : tests) {
            validations.add(handleTest(test));
        }

        return validations;
    }

    private Validation handleTest(Document document) throws ValidatorException {
        return handleTest(document.unmarshal(JAXB, Test.class));
    }

    private Validation handleTest(Test test) throws ValidatorException {
        if (test.getAny() instanceof Node node) {
            var document = Document.of(node).update(Collections.singletonList(test.getConfiguration()), null);

            return handleGeneric(document, new ValidatorTestExpectation(test));
        }

        throw new ValidatorException("Unable to read example XML.");
    }

    private Validation handleDocument(Document document) {
        return handleGeneric(document, new XmlExpectation(document));
    }

    private Validation handleGeneric(Document document, Expectation expectation) {
        var validation = validator.validate(document);

        for (var section : validation.getReport().getSection()) {
            for (var assertion : section.getAssertion()) {
                expectation.filterFlag(assertion);
            }

            expectation.verify((Section) section);
        }

        return validation;
    }
}
