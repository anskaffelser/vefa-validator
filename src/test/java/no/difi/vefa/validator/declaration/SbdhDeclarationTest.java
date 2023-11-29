package no.difi.vefa.validator.declaration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.service.DetectorService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SbdhDeclarationTest {

    @Inject
    private DetectorService declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule(null)).injectMembers(this);
    }

    @Test
    public void simpleSbdh() throws Exception {
        var document = Document.ofResource("/documents/peppol-bis-invoice-sbdh.xml");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getIdentifier().get(1), "SBDH:1.0");
        assertTrue(declarationIdentifier.hasChildren());
    }

    @Test
    public void simpleSbdhOnly() throws Exception {
        var document = Document.ofResource("/documents/sbdh-only.xml");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getIdentifier().get(1), "SBDH:1.0");
        assertFalse(declarationIdentifier.hasChildren());
    }
}
