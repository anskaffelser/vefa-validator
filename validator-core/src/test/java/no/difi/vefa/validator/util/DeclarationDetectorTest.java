package no.difi.vefa.validator.util;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.module.SaxonModule;
import no.difi.vefa.validator.module.SbdhModule;
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
        Guice.createInjector(new SaxonModule(), new SbdhModule(), new ValidatorModule())
                .injectMembers(this);
    }

    @Test
    public void simple() throws Exception {
        //noinspection ConstantConditions
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/ehf-invoice-2.0.xml"));

        InputStream inputStream = new ByteArrayInputStream(bytes);
        DeclarationIdentifier declarationIdentifier = declarationDetector.detect(inputStream);
        Assert.assertEquals(declarationIdentifier.getDeclaration().getType(), "xml.ubl");
        Assert.assertEquals(declarationIdentifier.getIdentifier().get(0), "urn:www.cenbii.eu:profile:bii05:ver2.0#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0");
        Assert.assertNotNull(declarationIdentifier.getParent());
    }
}
