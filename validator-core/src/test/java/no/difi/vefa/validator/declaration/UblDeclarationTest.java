package no.difi.vefa.validator.declaration;

import static org.testng.Assert.*;

import no.difi.vefa.validator.api.ValidatorException;
import org.testng.annotations.Test;

public class UblDeclarationTest {

    private UblDeclaration declaration = new UblDeclaration();

    @Test
    public void validNormal() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><ProfileID>profile</ProfileID><CustomizationID>customization</CustomizationID></xml>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validNormalNamespace1() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><cbc:ProfileID>profile</cbc:ProfileID><cbc:CustomizationID>customization</cbc:CustomizationID></xml>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validNormalNamespace2() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><ns1:ProfileID>profile</ns1:ProfileID><ns2:CustomizationID>customization</ns2:CustomizationID></xml>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validSpaces() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><ProfileID  >profile</ProfileID   ><CustomizationID   >customization</CustomizationID   ></xml>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void validTabs() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><ProfileID\t>profile</ProfileID\t><CustomizationID\t>customization</CustomizationID\t></xml>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#customization");
    }

    @Test
    public void invalidSpaces() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><  ProfileID>profile</ProfileID><  CustomizationID>customization</CustomizationID></xml>";
        assertTrue(declaration.verify(s));

        try {
            declaration.detect(s);
            fail("Exception expected.");
        } catch (ValidatorException e) {
            // No action
        }
    }

    @Test
    public void invalidTabs() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><\tProfileID>profile</ProfileID><\tCustomizationID>customization</CustomizationID></xml>";
        assertTrue(declaration.verify(s));

        try {
            declaration.detect(s);
            fail("Exception expected.");
        } catch (ValidatorException e) {
            // No action
        }
    }

    @Test
    public void validOioublDeclaration() throws Exception {
        String s = "<xml xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice\"><cbc:ProfileID schemeAgencyID=\"320\" schemeID=\"urn:oioubl:id:profileid-1.2\">Procurement-OrdSimR-BilSim-1.0</cbc:ProfileID><cbc:CustomizationID>OIOUBL-2.02</cbc:CustomizationID></xml>";
        assertTrue(declaration.verify(s));
        assertEquals(declaration.detect(s), "Procurement-OrdSimR-BilSim-1.0#OIOUBL-2.02");
    }

    @Test
    public void issue17() throws Exception {
        String s = "<xml><cbc:ProfileID>profile</cbc:ProfileID><cbc:CustomizationID schemeID=\"PEPPOL\">urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0</cbc:CustomizationID></xml>";
        assertFalse(declaration.verify(s));
        assertEquals(declaration.detect(s), "profile#urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0");
    }
}
