package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.api.CachedFile;
import no.difi.vefa.validator.util.DeclarationDetector;
import no.difi.vefa.validator.util.DeclarationIdentifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import static org.testng.Assert.*;

public class SbdhDeclarationTest {

    @Inject
    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector().injectMembers(this);
    }

    @Test
    public void simpleSbdh() throws Exception {
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.xml"));

        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(bytes);

        assertEquals(declarationIdentifier.getIdentifier(), "SBDH:1.0");

        Iterator<CachedFile> iterator = declarationIdentifier.getDeclaration().children(new ByteArrayInputStream(bytes)).iterator();
        assertTrue(iterator.hasNext());
    }

    @Test
    public void simpleSbdhOnly() throws Exception {
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/sbdh-only.xml"));

        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(bytes);

        assertEquals(declarationIdentifier.getIdentifier(), "SBDH:1.0");

        Iterator<CachedFile> iterator = declarationIdentifier.getDeclaration().children(new ByteArrayInputStream(bytes)).iterator();
        assertFalse(iterator.hasNext());
    }
}
