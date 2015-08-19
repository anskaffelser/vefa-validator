package no.difi.vefa.validator;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DocumentDeclarationTest {

    @Test
    public void validNormal() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<xml><ProfileID>profile</ProfileID><CustomizationID>customization</CustomizationID></xml>"
        );
        assertEquals(declaration.getCustomizationId(), "customization");
        assertEquals(declaration.getProfileId(), "profile");
    }

    @Test
    public void validNormalNamespace1() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<xml><cbc:ProfileID>profile</cbc:ProfileID><cbc:CustomizationID>customization</cbc:CustomizationID></xml>"
        );
        assertEquals(declaration.getCustomizationId(), "customization");
        assertEquals(declaration.getProfileId(), "profile");
    }

    @Test
    public void validNormalNamespace2() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<xml><ns1:ProfileID>profile</ns1:ProfileID><ns2:CustomizationID>customization</ns2:CustomizationID></xml>"
        );
        assertEquals(declaration.getCustomizationId(), "customization");
        assertEquals(declaration.getProfileId(), "profile");
    }

    @Test
    public void validSpaces() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<ml><ProfileID  >profile</ProfileID   ><CustomizationID   >customization</CustomizationID   ></xml>"
        );
        assertEquals(declaration.getCustomizationId(), "customization");
        assertEquals(declaration.getProfileId(), "profile");
    }

    @Test
    public void validTabs() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<xml><ProfileID\t>profile</ProfileID\t><CustomizationID\t>customization</CustomizationID\t></xml>"
        );
        assertEquals(declaration.getCustomizationId(), "customization");
        assertEquals(declaration.getProfileId(), "profile");
    }

    @Test
    public void invalidSpaces() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<xml><  ProfileID>profile</ProfileID><  CustomizationID>customization</CustomizationID></xml>"
        );
        assertNull(declaration.getCustomizationId());
        assertNull(declaration.getProfileId());
    }

    @Test
    public void invalidTabs() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<xml><\tProfileID>profile</ProfileID><\tCustomizationID>customization</CustomizationID></xml>"
        );
        assertNull(declaration.getCustomizationId());
        assertNull(declaration.getProfileId());
    }

    @Test
    public void validOioublDeclaration() {
        DocumentDeclaration declaration = new DocumentDeclaration(
                "<xml><cbc:ProfileID schemeAgencyID=\"320\" schemeID=\"urn:oioubl:id:profileid-1.2\">Procurement-OrdSimR-BilSim-1.0</cbc:ProfileID><cbc:CustomizationID>OIOUBL-2.02</cbc:CustomizationID></xml>"
        );
        assertEquals(declaration.getCustomizationId(), "OIOUBL-2.02");
        assertEquals(declaration.getProfileId(), "Procurement-OrdSimR-BilSim-1.0");
    }

    @SuppressWarnings("all")
    @Test
    public void equals() {
        DocumentDeclaration original = new DocumentDeclaration("c", "p");

        assertTrue(original.equals(new DocumentDeclaration("c", "p")));
        assertTrue(original.equals(original));
        assertFalse(original.equals(new DocumentDeclaration("c1", "p")));
        assertFalse(original.equals(new DocumentDeclaration("c", "p1")));
        assertFalse(original.equals(new Object()));
        assertFalse(original.equals(null));
    }
}
