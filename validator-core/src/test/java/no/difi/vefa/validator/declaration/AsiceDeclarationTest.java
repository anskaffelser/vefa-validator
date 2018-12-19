package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.asic.AsicVerifierFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AsiceDeclarationTest {

    private AsiceDeclaration declaration = new AsiceDeclaration();

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

        Assert.assertTrue(declaration.verify(byteArrayOutputStream.toByteArray(), null));

        ByteArrayOutputStream converted = new ByteArrayOutputStream();
        declaration.convert(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), converted);

        AsicVerifierFactory.newFactory().verify(new ByteArrayInputStream(converted.toByteArray()));
    }
}
