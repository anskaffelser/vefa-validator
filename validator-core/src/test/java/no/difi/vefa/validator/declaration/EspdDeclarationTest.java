package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.api.Declaration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

public class EspdDeclarationTest {

    private Declaration declaration = new EspdDeclaration("urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1", "ESPDResponse");

    @Test
    public void simple() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/ESPDResponse.xml"), byteArrayOutputStream);

        Assert.assertTrue(declaration.verify(byteArrayOutputStream.toByteArray()));
        Assert.assertEquals("urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse::1", declaration.detect(byteArrayOutputStream.toByteArray()));

    }

}
