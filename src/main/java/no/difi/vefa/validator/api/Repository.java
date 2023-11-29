package no.difi.vefa.validator.api;

import jakarta.xml.bind.JAXBContext;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.ArtifactHolder;
import no.difi.vefa.validator.model.ArtifactInfo;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.util.JaxbUtils;
import no.difi.xsd.vefa.validator._1.Artifacts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface Repository {

    JAXBContext JAXB = JaxbUtils.context(Artifacts.class);

    InputStream fetch(String path) throws IOException;

    default List<ArtifactInfo> fetchListing() throws IOException, ValidatorException {
        try (var inputStream = fetch("artifacts.xml")) {
            var artifacts = Document.of(inputStream).unmarshal(JAXB, Artifacts.class);

            return artifacts.getArtifact().stream()
                    .map(ArtifactInfo::of)
                    .toList();
        } catch (FileNotFoundException e) {
            return listingFallback();
        }
    }

    default List<ArtifactInfo> listingFallback() throws IOException {
        throw new IOException("Not implemented"); // TODO
    }

    default ArtifactHolder fetchArtifact(ArtifactInfo info) throws IOException {
        try (var inputStream = fetch(info.getPath())) {
            return ArtifactHolder.of(info, inputStream);
        }
    }
}
