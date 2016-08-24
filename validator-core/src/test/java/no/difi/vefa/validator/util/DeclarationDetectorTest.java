package no.difi.vefa.validator.util;

import com.google.common.io.ByteStreams;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DeclarationDetectorTest {

    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Config config = ConfigFactory.load();
        config = config.withFallback(config.getConfig("defaults"));

        declarationDetector = new DeclarationDetector(config);
    }

    @Test
    public void simple() throws Exception {
        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/ehf-invoice-2.0.xml")));

        Assert.assertEquals(declarationIdentifier.getDeclaration().getType(), "xml.ubl");
        Assert.assertEquals(declarationIdentifier.getIdentifier(), "urn:www.cenbii.eu:profile:bii05:ver2.0#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0");
        Assert.assertNotNull(declarationIdentifier.getParent());
    }
}
