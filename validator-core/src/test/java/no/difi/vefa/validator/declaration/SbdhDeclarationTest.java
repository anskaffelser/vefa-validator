package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

public class SbdhDeclarationTest {

    private SbdhDeclaration declaration = new SbdhDeclaration();

    @Test
    public void simpleSbdh() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.xml"), byteArrayOutputStream);

        Assert.assertTrue(declaration.verify(byteArrayOutputStream.toByteArray()));
        Assert.assertEquals(declaration.detect(byteArrayOutputStream.toByteArray()), "SBDH:1.0");
    }

    @Test
    public void simpleSbdhOnly() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/sbdh-only.xml"), byteArrayOutputStream);

        Assert.assertTrue(declaration.verify(byteArrayOutputStream.toByteArray()));
        Assert.assertEquals(declaration.detect(byteArrayOutputStream.toByteArray()), "SBDH:1.0");

        Iterator<InputStream> iterator = declaration.children(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void simpleUBL() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/T10-hode-feilkoder.xml"), byteArrayOutputStream);

        Assert.assertFalse(declaration.verify(byteArrayOutputStream.toByteArray()));
    }

    @Test
    public void simpleEdifact() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/edifact-invoic-d-97b-un.txt"), byteArrayOutputStream);

        Assert.assertFalse(declaration.verify(byteArrayOutputStream.toByteArray()));
    }

}
