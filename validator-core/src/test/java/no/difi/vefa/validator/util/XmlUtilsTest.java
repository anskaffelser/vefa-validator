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
    }

    @Test
    public void simpleConstructor() {
        new XmlUtils();
    }

}
