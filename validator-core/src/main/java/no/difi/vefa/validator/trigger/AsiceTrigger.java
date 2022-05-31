package no.difi.vefa.validator.trigger;

import no.difi.asic.AsicVerifier;
import no.difi.asic.AsicVerifierFactory;
import no.difi.commons.asic.jaxb.asic.Certificate;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Document;
import no.difi.vefa.validator.api.Section;
import no.difi.vefa.validator.api.Trigger;
import no.difi.xsd.vefa.validator._1.FlagType;

@Type("asice")
public class AsiceTrigger implements Trigger {

    private final static AsicVerifierFactory factory = AsicVerifierFactory.newFactory();

    @Override
    public void check(Document document, Section section) {
        try {
            section.setTitle("ASiC-E Verifier");
            AsicVerifier verifier = factory.verify(document.getInputStream());

            for (Certificate certificate : verifier.getAsicManifest().getCertificate())
                section.add("ASICE-001", String.format("Certificate: %s", certificate.getSubject()), FlagType.INFO);
        } catch (Exception e) {
            section.add("ASICE-002", e.getMessage(), FlagType.FATAL);
        }
    }
}
