package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.DeclarationWithChildren;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.Test;
import no.difi.xsd.vefa.validator._1.TestSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

public class ValidatorTestSetDeclaration extends SimpleXmlDeclaration implements DeclarationWithChildren {

    private static Logger logger = LoggerFactory.getLogger(ValidatorTestSetDeclaration.class);

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(TestSet.class, Test.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ValidatorTestSetDeclaration() {
        super("http://difi.no/xsd/vefa/validator/1.0", "testSet");
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }

    @Override
    public Iterable<InputStream> children(InputStream inputStream) throws ValidatorException {
        return new TestSetIterator(inputStream);
    }

    class TestSetIterator implements Iterator<InputStream>, Iterable<InputStream> {

        private TestSet testSet;
        private int counter = -1;

        public TestSetIterator(InputStream inputStream) throws ValidatorException {
            try {
                testSet = jaxbContext.createUnmarshaller().unmarshal(new StreamSource(inputStream), TestSet.class).getValue();
            } catch (JAXBException e) {
                throw new ValidatorException(e.getMessage(), e);
            }
        }

        @Override
        public Iterator<InputStream> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return ++counter < testSet.getTest().size();
        }

        @Override
        public InputStream next() {
            try {
                Test test = testSet.getTest().get(counter);

                if (test.getConfiguration() == null)
                    test.setConfiguration(testSet.getConfiguration());

                if (testSet.getAssert() != null) {
                    test.getAssert().getScope().addAll(testSet.getAssert().getScope());
                    test.getAssert().getFatal().addAll(testSet.getAssert().getFatal());
                    test.getAssert().getError().addAll(testSet.getAssert().getError());
                    test.getAssert().getWarning().addAll(testSet.getAssert().getWarning());
                    test.getAssert().getSuccess().addAll(testSet.getAssert().getSuccess());
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jaxbContext.createMarshaller().marshal(test, byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (JAXBException e) {
                logger.warn("Unable to marshall test object.");
            }
            return null;
        }

        @Override
        public void remove() {
            // No action.
        }
    }
}
