package no.difi.vefa.validator.expectation;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Document;
import no.difi.vefa.validator.util.JaxbUtils;
import no.difi.xsd.vefa.validator._1.AssertElementType;
import no.difi.xsd.vefa.validator._1.AssertType;
import no.difi.xsd.vefa.validator._1.Test;

import javax.xml.transform.stream.StreamSource;

@Slf4j
public class ValidatorTestExpectation extends AbstractExpectation {

    private static final JAXBContext jaxbContext = JaxbUtils.context(Test.class);

    public ValidatorTestExpectation(Document document) {
        try {
            Test test = jaxbContext.createUnmarshaller().unmarshal(
                    new StreamSource(document.asInputStream()), Test.class).getValue();
            AssertType assertType = test.getAssert();

            if (assertType != null) {
                description = test.getId() == null ?
                        assertType.getDescription() :
                        String.format("%s) %s", test.getId(), assertType.getDescription());
                scopes.addAll(assertType.getScope());

                for (AssertElementType a : assertType.getFatal())
                    fatals.put(a.getValue(), a.getNumber() == null ? 1 : a.getNumber());
                for (AssertElementType a : assertType.getError())
                    errors.put(a.getValue(), a.getNumber() == null ? 1 : a.getNumber());
                for (AssertElementType a : assertType.getWarning())
                    warnings.put(a.getValue(), a.getNumber() == null ? 1 : a.getNumber());
                for (String s : assertType.getSuccess())
                    successes.put(s, 1);
            }

        } catch (JAXBException e) {
            log.warn(e.getMessage(), e);
        }
    }
}
