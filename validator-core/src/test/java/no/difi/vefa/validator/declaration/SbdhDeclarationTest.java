package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

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
        byte[] bytes;

        try (InputStream inputStream = getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.xml")) {
            bytes = ByteStreams.toByteArray(inputStream);
        }

        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(bytes);

        assertEquals(declarationIdentifier.getIdentifier().get(1), "SBDH:1.0");

        Iterator<CachedFile> iterator = declarationIdentifier.getDeclaration().children(new ByteArrayInputStream(bytes)).iterator();
        assertTrue(iterator.hasNext());
    }

    @Test
    public void simpleSbdhOnly() throws Exception {
        byte[] bytes;

        try (InputStream inputStream = getClass().getResourceAsStream("/documents/sbdh-only.xml")) {
            bytes = ByteStreams.toByteArray(inputStream);
        }

        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(bytes);

        assertEquals(declarationIdentifier.getIdentifier().get(1), "SBDH:1.0");

        Iterator<CachedFile> iterator = declarationIdentifier.getDeclaration().children(new ByteArrayInputStream(bytes)).iterator();
        assertFalse(iterator.hasNext());
    }
}
