package no.difi.vefa.validator.trigger;

import no.difi.asic.AsicVerifier;
import no.difi.asic.AsicVerifierFactory;
import no.difi.vefa.validator.api.*;
import no.difi.xsd.asic.model._1.Certificate;
import no.difi.xsd.vefa.validator._1.FlagType;

@TriggerInfo("asice")
public class AsiceTrigger implements Trigger {

    private static AsicVerifierFactory factory = AsicVerifierFactory.newFactory();

    @Override
    public void check(Document document, Section section) throws ValidatorException {
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
