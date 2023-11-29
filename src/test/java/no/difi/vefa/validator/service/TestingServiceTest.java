package no.difi.vefa.validator.service;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.util.Repositories;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestingServiceTest {

    @Inject
    private TestingService testingService;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule(Repositories.classpath("/rules/"))).injectMembers(this);
    }

    @Test
    public void testSetTest() throws Exception {
        var document = Document.ofResource("/documents/NOGOV-T10-R014.xml");

        var validations = testingService.verify(document);
    }

    // @Test
    public void genericTest() throws Exception {
        var document = Document.ofResource("/documents/T10-hode-feilkoder.xml");

        var validations = testingService.verify(document);
    }
}
