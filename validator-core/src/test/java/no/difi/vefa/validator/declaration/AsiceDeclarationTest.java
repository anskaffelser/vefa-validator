package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.api.Declaration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

public class AsiceDeclarationTest {

    private Declaration declaration = new AsiceDeclaration();

    @Test
    public void validFile() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/asic-cades-test-valid.asice"), byteArrayOutputStream);

        Assert.assertTrue(declaration.verify(byteArrayOutputStream.toByteArray()));
    }

    @Test
    public void invalidFile() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.zip"), byteArrayOutputStream);

        Assert.assertFalse(declaration.verify(byteArrayOutputStream.toByteArray()));
    }

}
