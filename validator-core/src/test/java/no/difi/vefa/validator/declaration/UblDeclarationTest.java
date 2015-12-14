package no.difi.vefa.validator.declaration;

import static org.testng.Assert.*;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.api.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

public class UblDeclarationTest {

    private static Logger logger = LoggerFactory.getLogger(UblDeclarationTest.class);

    private UblDeclaration declaration = new UblDeclaration();

    private String docStart = "<Invoice:Invoice xmlns:Invoice=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"";

    @Test
    public void validNormal() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><CustomizationID>customization</CustomizationID><ProfileID>profile</ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validNormalNamespace1() throws Exception {
        String s = docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><cbc:CustomizationID>customization</cbc:CustomizationID><cbc:ProfileID>profile</cbc:ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validNormalNamespace2() throws Exception {
        String s = docStart + " xmlns:ns1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><ns1:CustomizationID>customization</ns1:CustomizationID><ns1:ProfileID>profile</ns1:ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validSpaces() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><CustomizationID   >customization</CustomizationID   ><ProfileID  >profile</ProfileID   ></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validTabs() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><CustomizationID\t>customization</CustomizationID\t><ProfileID\t>profile</ProfileID\t></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void invalidSpaces() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><  CustomizationID>customization</CustomizationID><  ProfileID>profile</ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));

        try {
            declaration.detect(s);
            fail("Exception expected.");
        } catch (ValidatorException e) {
            logger.info(e.getMessage());
        }
    }

    @Test
    public void invalidTabs() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><\tCustomizationID>customization</CustomizationID><\tProfileID>profile</ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));

        try {
            declaration.detect(s);
            fail("Exception expected.");
        } catch (ValidatorException e) {
            logger.info(e.getMessage());
        }
    }

    @Test
    public void validOioublDeclaration() throws Exception {
        String s = docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><cbc:CustomizationID>OIOUBL-2.02</cbc:CustomizationID><cbc:ProfileID schemeAgencyID=\"320\" schemeID=\"urn:oioubl:id:profileid-1.2\">Procurement-OrdSimR-BilSim-1.0</cbc:ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "Procurement-OrdSimR-BilSim-1.0#OIOUBL-2.02");
    }

    @Test
    public void issue17() throws Exception {
        String s = docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><cbc:CustomizationID schemeID=\"PEPPOL\">urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0</cbc:CustomizationID> <cbc:ProfileID>profile</cbc:ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0");
    }

    @Test(enabled = false)
    public void invalidSchemaLocation() throws Exception {
        String sl = " schemaLocation=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2 D:/MapForcewspc/MapForce.InvoiciaXML/Schemes/UBL 2.1/maindoc/UBL-Invoice-2.1.xsd\"";
        String s = docStart + sl + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"><CustomizationID>customization</CustomizationID><ProfileID>profile</ProfileID></Invoice:Invoice>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void invalidSbdhDocument() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.xml"), byteArrayOutputStream);

        assertFalse(declaration.verify(byteArrayOutputStream.toString()));
    }

    @Test
    public void validDocument() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/T10-hode-feilkoder.xml"), byteArrayOutputStream);

        assertTrue(declaration.verify(byteArrayOutputStream.toString()));
    }

    @Test
    public void invalidEdiface() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteStreams.copy(getClass().getResourceAsStream("/documents/edifact-invoic-d-97b-un.txt"), byteArrayOutputStream);

        assertFalse(declaration.verify(byteArrayOutputStream.toString()));
    }
}
