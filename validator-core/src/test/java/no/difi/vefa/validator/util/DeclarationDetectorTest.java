package no.difi.vefa.validator.util;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DeclarationDetectorTest {

    @Inject
    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector().injectMembers(this);
    }

    @Test
    public void simple() throws Exception {
        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/ehf-invoice-2.0.xml")));

        Assert.assertEquals(declarationIdentifier.getDeclaration().getType(), "xml.ubl");
        Assert.assertEquals(declarationIdentifier.getIdentifier(), "urn:www.cenbii.eu:profile:bii05:ver2.0#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0");
        Assert.assertNotNull(declarationIdentifier.getParent());
    }
}
