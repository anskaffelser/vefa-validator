package no.difi.vefa.validator.api;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DocumentTest {

    @Test
    public void simple() throws IOException {
        Document document = new Document(new ByteArrayInputStream(new byte[] {}), "identifier", null);

        Assert.assertNotNull(document.asInputStream());
        Assert.assertEquals(document.getDeclarations().get(0), "identifier");
        Assert.assertNull(document.getExpectation());
    }

}
