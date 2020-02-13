package no.difi.vefa.validator.declaration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.api.CachedFile;
import no.difi.vefa.validator.module.SaxonModule;
import no.difi.vefa.validator.module.SbdhModule;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.util.DeclarationDetector;
import no.difi.vefa.validator.util.DeclarationIdentifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import static no.difi.vefa.validator.util.StreamUtils.readAllAndReset;
import static org.testng.Assert.*;

public class SbdhDeclarationTest {

    @Inject
    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new SaxonModule(), new SbdhModule(), new ValidatorModule())
                .injectMembers(this);
    }

    @Test
    public void simpleSbdh() throws Exception {

        try (InputStream inputStream = new BufferedInputStream(getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.xml"))) {
            DeclarationIdentifier declarationIdentifier = declarationDetector.detect(inputStream);
            assertEquals(declarationIdentifier.getIdentifier().get(1), "SBDH:1.0");
            Iterator<CachedFile> iterator = declarationIdentifier.getDeclaration().children(inputStream).iterator();
            assertTrue(iterator.hasNext());
        }
    }

    @Test
    public void simpleSbdhOnly() throws Exception {

        try (InputStream inputStream = new BufferedInputStream(getClass().getResourceAsStream("/documents/sbdh-only.xml"))) {
            DeclarationIdentifier declarationIdentifier = declarationDetector.detect(inputStream);
            assertEquals(declarationIdentifier.getIdentifier().get(1), "SBDH:1.0");
            Iterator<CachedFile> iterator = declarationIdentifier.getDeclaration().children(inputStream).iterator();
            assertFalse(iterator.hasNext());
        }
    }
}
