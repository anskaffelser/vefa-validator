package no.difi.vefa.validator.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class XmlUtilsTest {

    @Test
    public void localNameTest() {
        String xml = "<test xmlns=\"http://difi.no/xsd/vefa/validator/1.0\" configuration=\"ehf-t10-base-2.0\">";
        Assert.assertEquals(XmlUtils.extractRootNamespace(xml), "http://difi.no/xsd/vefa/validator/1.0");
        Assert.assertEquals(XmlUtils.extractLocalName(xml), "test");

        xml = "<ubl:Invoice\n" +
                "            xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"\n" +
                "            xmlns:ubl=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"\n" +
                "                     xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"\n" +
                "                     xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"\n" +
                "                     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
        Assert.assertEquals(XmlUtils.extractRootNamespace(xml), "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        Assert.assertEquals(XmlUtils.extractLocalName(xml), "Invoice");

        xml = "<espd-req:ESPDRequest xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:cev-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1\" xmlns:ccv-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1\" xmlns:cev=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonAggregateComponents-1\" xmlns:espd-cac=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1\" xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" xmlns:espd-cbc=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1\" xmlns:ccv=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1\" xmlns:espd-req=\"urn:grow:names:specification:ubl:schema:xsd:ESPDRequest-1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:espd=\"urn:grow:names:specification:ubl:schema:xsd:ESPDRequest-1\" xsi:schemaLocation=\"urn:grow:names:specification:ubl:schema:xsd:ESPDRequest-1 ../xsdrt/maindoc/ESPDRequest-1.0.xsd\">";
        Assert.assertEquals(XmlUtils.extractRootNamespace(xml), "urn:grow:names:specification:ubl:schema:xsd:ESPDRequest-1");
        Assert.assertEquals(XmlUtils.extractLocalName(xml), "ESPDRequest");

    }

    @Test
    public void simpleConstructor() {
        new XmlUtils();
    }

}
