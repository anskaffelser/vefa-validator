package no.difi.vefa.validator.source;

import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Slf4j
class RepositorySourceInstance extends AbstractSourceInstance {

    public RepositorySourceInstance(List<URI> rootUris) throws ValidatorException {
        try {
            for (URI rootUri : rootUris) {
                Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
                URI artifactsUri = rootUri.resolve("artifacts.xml");
                log.info(String.format("Fetching %s", artifactsUri));
                Artifacts artifactsType = (Artifacts) unmarshaller.unmarshal(artifactsUri.toURL());

                for (ArtifactType artifact : artifactsType.getArtifact()) {
                    URI artifactUri = rootUri.resolve(artifact.getFilename());
                    log.info(String.format("Fetching %s", artifactUri));
                    try (InputStream inputStream = artifactUri.toURL().openStream()) {
                        unpackContainer(inputStream, artifact.getFilename());
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
