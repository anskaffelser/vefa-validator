package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.util.DeclarationDetector;
import no.difi.vefa.validator.util.DeclarationIdentifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class UblDeclarationTest {

    private final String docStart =
            "<Invoice:Invoice xmlns:Invoice=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"";

    @Inject
    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule()).injectMembers(this);
    }

    @Test
    public void validNormal() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID>customization</CustomizationID><ProfileID>profile</ProfileID></Invoice:Invoice>";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());

        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validNormalNamespace1() throws Exception {
        String s = docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<cbc:CustomizationID>customization</cbc:CustomizationID>" +
                "<cbc:ProfileID>profile</cbc:ProfileID>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validNormalNamespace2() throws Exception {
        String s = docStart + " xmlns:ns1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<ns1:CustomizationID>customization</ns1:CustomizationID>" +
                "<ns1:ProfileID>profile</ns1:ProfileID>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validSpaces() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID   >customization</CustomizationID   >" +
                "<ProfileID  >profile</ProfileID   >" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validTabs() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID\t>customization</CustomizationID\t>" +
                "<ProfileID\t>profile</ProfileID\t>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void invalidSpaces() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<  CustomizationID>customization</CustomizationID>" +
                "<  ProfileID>profile</ProfileID>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getDeclaration().getType(), "xml");
    }

    @Test
    public void invalidTabs() throws Exception {
        String s = docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<\tCustomizationID>customization</CustomizationID>" +
                "<\tProfileID>profile</ProfileID>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getDeclaration().getType(), "xml");
    }

    @Test
    public void validOioublDeclaration() throws Exception {
        String s = docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<cbc:CustomizationID>OIOUBL-2.02</cbc:CustomizationID>" +
                "<cbc:ProfileID " +
                "schemeAgencyID=\"320\" " +
                "schemeID=\"urn:oioubl:id:profileid-1.2\">Procurement-OrdSimR-BilSim-1.0</cbc:ProfileID>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0),
                "Procurement-OrdSimR-BilSim-1.0#OIOUBL-2.02");
    }

    @Test
    public void issue17() throws Exception {
        String s = docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<cbc:CustomizationID " +
                "schemeID=\"PEPPOL\">urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:" +
                "urn:www.peppol.eu:bis:peppol4a:ver2.0</cbc:CustomizationID> " +
                "<cbc:ProfileID>profile</cbc:ProfileID>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0),
                "profile#" +
                        "urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:" +
                        "urn:www.peppol.eu:bis:peppol4a:ver2.0");

    }

    @Test(enabled = false)
    public void invalidSchemaLocation() throws Exception {
        String sl = " schemaLocation=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2 " +
                "D:/MapForcewspc/MapForce.InvoiciaXML/Schemes/UBL 2.1/maindoc/UBL-Invoice-2.1.xsd\"";
        String s = docStart + sl + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID>customization</CustomizationID>" +
                "<ProfileID>profile</ProfileID>" +
                "</Invoice:Invoice>";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        assertEquals(declarationDetector.detect(inputStream).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void invalidSbdhDocument() throws Exception {
        try (InputStream inputStream = new BufferedInputStream(getClass().getResourceAsStream("/documents/peppol-bis-invoice-sbdh.xml"))) {
            assertNotEquals(declarationDetector.detect(inputStream).getDeclaration().getType(), "xml.ubl");
        }
    }


    @Test
    public void validDocument() throws Exception {
        try (InputStream inputStream = new BufferedInputStream(getClass().getResourceAsStream("/documents/T10-hode-feilkoder.xml"))) {
            assertEquals(declarationDetector.detect(inputStream).getDeclaration().getType(), "xml.ubl");
        }
    }

    @Test
    public void invalidEdifact() throws Exception {
        try (InputStream inputStream = new BufferedInputStream(getClass().getResourceAsStream("/documents/edifact-invoic-d-97b-un.txt"))) {
            assertEquals(declarationDetector.detect(inputStream), DeclarationDetector.UNKNOWN);
        }
    }

    @Test
    public void emptyElements() throws Exception {
        String xml = "<test><CustomizationID></CustomizationID><ProfileID></ProfileID></test>";
        assertEquals(declarationDetector.detect(new ByteArrayInputStream(xml.getBytes())), DeclarationDetector.UNKNOWN);
    }

    @Test
    public void customizationOnly() throws Exception {
        String xml = "<test><CustomizationID>Test</CustomizationID></test>";
        assertEquals(declarationDetector.detect(new ByteArrayInputStream(xml.getBytes())), DeclarationDetector.UNKNOWN);
    }

    @Test
    public void incompleteDeclaration() throws Exception {
        String xml = "<test>";
        assertEquals(declarationDetector.detect(new ByteArrayInputStream(xml.getBytes())), DeclarationDetector.UNKNOWN);
    }

    @Test
    public void withoutDeclaration() throws Exception {
        String xml = "<test></test>";
        assertEquals(declarationDetector.detect(new ByteArrayInputStream(xml.getBytes())), DeclarationDetector.UNKNOWN);
    }

    @Test
    public void eforms() throws Exception {
        byte[] bytes = ByteStreams.toByteArray(getClass().getResourceAsStream("/documents/eforms-cn_23.xml"));

        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            DeclarationIdentifier declarationIdentifier = declarationDetector.detect(inputStream);
            assertEquals(declarationIdentifier.getDeclaration().getType(), "xml.ubl");
        }
    }
}
