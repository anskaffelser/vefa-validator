package no.difi.vefa.validator.util;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.api.Document;
import no.difi.vefa.validator.module.ValidatorModule;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DeclarationDetectorTest {

    @Inject
    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule()).injectMembers(this);
    }

    @Test
    public void simple() throws Exception {
        var document = Document.ofResource("/documents/ehf-invoice-2.0.xml");

        var declarationIdentifier = declarationDetector.detect(document);
        Assert.assertEquals(declarationIdentifier.getType(), "xml.ubl");
        Assert.assertEquals(declarationIdentifier.getIdentifier().get(0), "urn:www.cenbii.eu:profile:bii05:ver2.0#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0");
    }
}
