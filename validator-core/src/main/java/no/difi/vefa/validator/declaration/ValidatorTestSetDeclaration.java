package no.difi.vefa.validator.declaration;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.Test;
import no.difi.xsd.vefa.validator._1.TestSet;
import org.kohsuke.MetaInfServices;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

@Slf4j
@MetaInfServices(Declaration.class)
public class ValidatorTestSetDeclaration extends SimpleXmlDeclaration implements DeclarationWithChildren {

    private static JAXBContext jaxbContext = JAXBHelper.context(TestSet.class, Test.class);

    public ValidatorTestSetDeclaration() {
        super("http://difi.no/xsd/vefa/validator/1.0", "testSet");
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }

    @Override
    public Iterable<CachedFile> children(InputStream inputStream) throws ValidatorException {
        return new TestSetIterator(inputStream);
    }

    class TestSetIterator implements Iterator<CachedFile>, Iterable<CachedFile> {

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
        public Iterator<CachedFile> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return ++counter < testSet.getTest().size();
        }

        @Override
        public CachedFile next() {
            try {
                Test test = testSet.getTest().get(counter);

                if (test.getConfiguration() == null)
                    test.setConfiguration(testSet.getConfiguration());

                if (test.getId() == null)
                    test.setId(String.valueOf(counter + 1));

                if (testSet.getAssert() != null) {
                    test.getAssert().getScope().addAll(testSet.getAssert().getScope());
                    test.getAssert().getFatal().addAll(testSet.getAssert().getFatal());
                    test.getAssert().getError().addAll(testSet.getAssert().getError());
                    test.getAssert().getWarning().addAll(testSet.getAssert().getWarning());
                    test.getAssert().getSuccess().addAll(testSet.getAssert().getSuccess());

                    if (test.getAssert().getDescription() == null)
                        test.getAssert().setDescription(testSet.getAssert().getDescription());
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jaxbContext.createMarshaller().marshal(test, byteArrayOutputStream);
                return new CachedFile(byteArrayOutputStream.toByteArray());
            } catch (JAXBException e) {
                log.warn("Unable to marshall test object.");
            }
            return null;
        }

        @Override
        public void remove() {
            // No action.
        }
    }
}
