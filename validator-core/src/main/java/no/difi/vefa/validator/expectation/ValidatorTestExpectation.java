package no.difi.vefa.validator.expectation;

import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.AssertElementType;
import no.difi.xsd.vefa.validator._1.AssertType;
import no.difi.xsd.vefa.validator._1.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

public class ValidatorTestExpectation extends AbstractExpectation {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(ValidatorTestExpectation.class);
    private static JAXBContext jaxbContext = JAXBHelper.context(Test.class);

    public ValidatorTestExpectation(byte[] bytes) {
        try {
            Test test = jaxbContext.createUnmarshaller().unmarshal(new StreamSource(new ByteArrayInputStream(bytes)), Test.class).getValue();
            AssertType assertType = test.getAssert();

            if (assertType != null) {
                description = assertType.getDescription();
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
            logger.warn(e.getMessage(), e);
        }
    }
}
