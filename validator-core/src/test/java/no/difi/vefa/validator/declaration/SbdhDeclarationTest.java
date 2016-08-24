package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import no.difi.vefa.validator.util.DeclarationDetector;
import no.difi.vefa.validator.util.DeclarationIdentifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

import static org.testng.Assert.*;

public class SbdhDeclarationTest {

    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Config config = ConfigFactory.load();
        config = config.withFallback(config.getConfig("defaults"));

        declarationDetector = new DeclarationDetector(config);
    }

    @Test
    public void simpleSbdh() throws Exception {
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.xml"));

        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(bytes);

        assertEquals(declarationIdentifier.getIdentifier(), "SBDH:1.0");

        Iterator<InputStream> iterator = declarationIdentifier.getDeclaration().children(new ByteArrayInputStream(bytes)).iterator();
        assertTrue(iterator.hasNext());
    }

    @Test
    public void simpleSbdhOnly() throws Exception {
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/sbdh-only.xml"));

        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(bytes);

        assertEquals(declarationIdentifier.getIdentifier(), "SBDH:1.0");

        Iterator<InputStream> iterator = declarationIdentifier.getDeclaration().children(new ByteArrayInputStream(bytes)).iterator();
        assertFalse(iterator.hasNext());
    }
}
