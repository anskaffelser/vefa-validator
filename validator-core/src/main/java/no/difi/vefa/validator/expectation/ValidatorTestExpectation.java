package no.difi.vefa.validator.expectation;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.AssertElementType;
import no.difi.xsd.vefa.validator._1.AssertType;
import no.difi.xsd.vefa.validator._1.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

@Slf4j
public class ValidatorTestExpectation extends AbstractExpectation {

    private static JAXBContext jaxbContext = JAXBHelper.context(Test.class);

    public ValidatorTestExpectation(byte[] bytes) {
        try {
            Test test = jaxbContext.createUnmarshaller().unmarshal(
                    new StreamSource(new ByteArrayInputStream(bytes)), Test.class).getValue();
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
