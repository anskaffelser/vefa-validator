package no.difi.vefa.validator.declaration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.service.DetectorService;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class EspdDeclarationTest {

    @Inject
    private DetectorService declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule(null)).injectMembers(this);
    }

    @Test
    public void simple() throws Exception {
        var document = Document.ofResource("/documents/ESPDResponse-2.xml");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getIdentifier().get(0),
                "urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse::SomeCustomization");
    }

    @Test
    public void simpleCustomization() throws Exception {
        var document = Document.ofResource("/documents/ESPDResponse.xml");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getIdentifier().get(0),
                "urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse::1");
    }

    @Test
    public void simpleVeryShort() throws Exception {
        var document = Document.of("<espd:ESPDResponse " +
                "xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" " +
                "xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" " +
                "xmlns:cev-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1\" " +
                "xmlns:ccv-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1\" " +
                "xmlns:cev=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonAggregateComponents-1\" " +
                "xmlns:espd-cac=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1\" " +
                "xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" " +
                "xmlns:espd-cbc=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1\" " +
                "xmlns:ccv=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1\" " +
                "xmlns:espd-res=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:espd=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\"></espd:ESPDResponse>");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getIdentifier().get(0),
                "urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse");
    }

    @Test
    public void simpleEmptyVersion() throws Exception {
        var document = Document.of("<espd:ESPDResponse " +
                "xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" " +
                "xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" " +
                "xmlns:cev-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1\" " +
                "xmlns:ccv-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1\" " +
                "xmlns:cev=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonAggregateComponents-1\" " +
                "xmlns:espd-cac=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1\" " +
                "xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" " +
                "xmlns:espd-cbc=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1\" " +
                "xmlns:ccv=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1\" " +
                "xmlns:espd-res=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:espd=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\">" +
                "<VersionID></VersionID></espd:ESPDResponse>");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getIdentifier().get(0),
                "urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse");
    }

    @Test
    public void simpleWithoutVersion() throws Exception {
        var document = Document.of("<espd:ESPDResponse " +
                "xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" " +
                "xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" " +
                "xmlns:cev-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1\" " +
                "xmlns:ccv-cbc=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1\" " +
                "xmlns:cev=\"urn:isa:names:specification:ubl:schema:xsd:CEV-CommonAggregateComponents-1\" " +
                "xmlns:espd-cac=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1\" " +
                "xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\" " +
                "xmlns:espd-cbc=\"urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1\" " +
                "xmlns:ccv=\"urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1\" " +
                "xmlns:espd-res=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:espd=\"urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1\">" +
                "</espd:ESPDResponse>");

        var declarationIdentifier = declarationDetector.detect(document);
        assertEquals(declarationIdentifier.getIdentifier().get(0),
                "urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1::ESPDResponse");
    }
}
