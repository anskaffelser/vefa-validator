package no.difi.vefa.validator.declaration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.service.DetectorService;
import no.difi.vefa.validator.model.Detected;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class UblDeclarationTest {

    private final String docStart =
            "<Invoice:Invoice xmlns:Invoice=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"";

    @Inject
    private DetectorService declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule()).injectMembers(this);
    }

    @Test
    public void validNormal() throws Exception {
        var document = Document.of(docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID>customization</CustomizationID><ProfileID>profile</ProfileID></Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validNormalNamespace1() throws Exception {
        var document = Document.of(docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<cbc:CustomizationID>customization</cbc:CustomizationID>" +
                "<cbc:ProfileID>profile</cbc:ProfileID>" +
                "</Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validNormalNamespace2() throws Exception {
        var document = Document.of(docStart + " xmlns:ns1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<ns1:CustomizationID>customization</ns1:CustomizationID>" +
                "<ns1:ProfileID>profile</ns1:ProfileID>" +
                "</Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validSpaces() throws Exception {
        var document = Document.of(docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID   >customization</CustomizationID   >" +
                "<ProfileID  >profile</ProfileID   >" +
                "</Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validTabs() throws Exception {
        var document = Document.of(docStart + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID\t>customization</CustomizationID\t>" +
                "<ProfileID\t>profile</ProfileID\t>" +
                "</Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void validOioublDeclaration() throws Exception {
        var document = Document.of(docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<cbc:CustomizationID>OIOUBL-2.02</cbc:CustomizationID>" +
                "<cbc:ProfileID " +
                "schemeAgencyID=\"320\" " +
                "schemeID=\"urn:oioubl:id:profileid-1.2\">Procurement-OrdSimR-BilSim-1.0</cbc:ProfileID>" +
                "</Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0),
                "Procurement-OrdSimR-BilSim-1.0#OIOUBL-2.02");
    }

    @Test
    public void issue17() throws Exception {
        var document = Document.of(docStart + " xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<cbc:CustomizationID " +
                "schemeID=\"PEPPOL\">urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:" +
                "urn:www.peppol.eu:bis:peppol4a:ver2.0</cbc:CustomizationID> " +
                "<cbc:ProfileID>profile</cbc:ProfileID>" +
                "</Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0),
                "profile#" +
                        "urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:" +
                        "urn:www.peppol.eu:bis:peppol4a:ver2.0");

    }

    @Test(enabled = false)
    public void invalidSchemaLocation() throws Exception {
        String sl = " schemaLocation=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2 " +
                "D:/MapForcewspc/MapForce.InvoiciaXML/Schemes/UBL 2.1/maindoc/UBL-Invoice-2.1.xsd\"";
        var document = Document.of(docStart + sl + " xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">" +
                "<CustomizationID>customization</CustomizationID>" +
                "<ProfileID>profile</ProfileID>" +
                "</Invoice:Invoice>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "profile#customization");
    }

    @Test
    public void invalidSbdhDocument() throws Exception {
        var document = Document.ofResource("/documents/peppol-bis-invoice-sbdh.xml");

        assertNotEquals(declarationDetector.detect(document).getType(), "xml.ubl");
    }


    @Test
    public void validDocument() throws Exception {
        var document = Document.ofResource("/documents/T10-hode-feilkoder.xml");

        assertEquals(declarationDetector.detect(document).getType(), "xml.ubl");
    }

    @Test
    public void invalidEdifact() throws Exception {
        var document = Document.ofResource("/documents/edifact-invoic-d-97b-un.txt");

        assertEquals(declarationDetector.detect(document), Detected.UNKNOWN);
    }

    @Test
    public void emptyElements() throws Exception {
        var document = Document.of("<test><CustomizationID></CustomizationID><ProfileID></ProfileID></test>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "test");
    }

    @Test
    public void customizationOnly() throws Exception {
        var document = Document.of("<test><CustomizationID>Test</CustomizationID></test>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "test");
    }

    @Test
    public void incompleteDeclaration() throws Exception {
        var document = Document.of("<test>");

        assertEquals(declarationDetector.detect(document), Detected.UNKNOWN);
    }

    @Test
    public void withoutDeclaration() throws Exception {
        var document = Document.of("<test></test>");

        assertEquals(declarationDetector.detect(document).getIdentifier().get(0), "test");
    }

    @Test
    public void eforms() throws Exception {
        var document = Document.ofResource("/documents/eforms-cn_23.xml");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getType(), "xml.ubl");
    }
}
