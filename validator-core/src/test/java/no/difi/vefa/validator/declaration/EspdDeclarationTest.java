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

    @Test
    public void simpleVeryShort() throws Exception {
        String xml = "<espd:ESPDResponse xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:cev-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1\" xmlns:ccv-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1\" xmlns:cev=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonAggregateComponents-1\" xmlns:espd-cac=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1\" xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:espd-cbc=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1\" xmlns:ccv=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1\" xmlns:espd-res=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:espd=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" xsi:schemaLocation=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1 ../xsdrt/maindoc/ESPDResponse-1.0.xsd\">";
        Assert.assertTrue(declaration.verify(xml.getBytes()));
        Assert.assertEquals("urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse", declaration.detect(xml.getBytes()));
    }

    @Test
    public void simpleEmptyVersion() throws Exception {
        String xml = "<espd:ESPDResponse xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:cev-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1\" xmlns:ccv-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1\" xmlns:cev=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonAggregateComponents-1\" xmlns:espd-cac=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1\" xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:espd-cbc=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1\" xmlns:ccv=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1\" xmlns:espd-res=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:espd=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" xsi:schemaLocation=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1 ../xsdrt/maindoc/ESPDResponse-1.0.xsd\"><VersionID></VersionID>";
        Assert.assertTrue(declaration.verify(xml.getBytes()));
        Assert.assertEquals("urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse", declaration.detect(xml.getBytes()));
    }

    @Test
    public void simpleWithoutVersion() throws Exception {
        String xml = "<espd:ESPDResponse xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:cev-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1\" xmlns:ccv-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1\" xmlns:cev=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonAggregateComponents-1\" xmlns:espd-cac=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1\" xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:espd-cbc=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1\" xmlns:ccv=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1\" xmlns:espd-res=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:espd=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" xsi:schemaLocation=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1 ../xsdrt/maindoc/ESPDResponse-1.0.xsd\"></espd:ESPDResponse>";
        Assert.assertTrue(declaration.verify(xml.getBytes()));
        Assert.assertEquals("urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse", declaration.detect(xml.getBytes()));
    }

}
