package no.difi.vefa.validator.checker;

import no.difi.asic.AsicVerifier;
import no.difi.asic.AsicVerifierFactory;
import no.difi.vefa.validator.api.*;
import no.difi.xsd.asic.model._1.Certificate;
import no.difi.xsd.vefa.validator._1.FlagType;

import java.nio.file.Path;

@CheckerInfo({".asice.trigger"})
public class AsiceTriggerChecker implements Checker {

    private AsicVerifierFactory factory = AsicVerifierFactory.newFactory();

    @Override
    public void prepare(Path path) throws ValidatorException {
        // No action
    }

    @Override
    public void check(Document document, Section section) throws ValidatorException {
        try {
            section.setTitle("ASiC-E Verifier");
            AsicVerifier verifier = factory.verify(document.getInputStream());

            for (Certificate certificate : verifier.getAsicManifest().getCertificates())
                section.add("ASICE-001", String.format("Certificate: %s", certificate.getSubject()), FlagType.INFO);
        } catch (Exception e) {
            section.add("ASICE-002", e.getMessage(), FlagType.FATAL);
        }
    }
}
