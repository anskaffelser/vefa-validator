package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.asic.AsicVerifierFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;

public class AsiceDeclarationTest {

    private AsiceDeclaration declaration = new AsiceDeclaration();

    private AsiceXmlDeclaration xmlDeclaration = new AsiceXmlDeclaration();

    @Test
    public void validFile() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = getClass().getResourceAsStream("/documents/asic-cades-test-valid.asice")) {
            ByteStreams.copy(inputStream, byteArrayOutputStream);
        }

        Assert.assertTrue(declaration.verify(byteArrayOutputStream.toByteArray(), null));
    }

    @Test
    public void invalidFile() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.zip")) {
            ByteStreams.copy(inputStream, byteArrayOutputStream);
        }

        Assert.assertFalse(declaration.verify(byteArrayOutputStream.toByteArray(), null));
    }

    @Test
    public void simpleXmlFile() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = getClass().getResourceAsStream("/documents/asic-xml.xml")) {
            ByteStreams.copy(inputStream, byteArrayOutputStream);
        }

        Assert.assertTrue(xmlDeclaration.verify(byteArrayOutputStream.toByteArray(),
                Collections.singletonList("urn:etsi.org:specification:02918:v1.2.1::asic")));

        ByteArrayOutputStream converted = new ByteArrayOutputStream();
        xmlDeclaration.convert(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), converted);

        AsicVerifierFactory.newFactory().verify(new ByteArrayInputStream(converted.toByteArray()));
    }
}
