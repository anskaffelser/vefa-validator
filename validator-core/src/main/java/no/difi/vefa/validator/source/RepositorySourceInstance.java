package no.difi.vefa.validator.source;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;

import javax.xml.bind.Unmarshaller;
import java.net.URI;

@Slf4j
class RepositorySourceInstance extends AbstractSourceInstance {

    public RepositorySourceInstance(Properties properties, URI rootUri) throws ValidatorException {
        super(properties);

        try {
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            URI artifactsUri = rootUri.resolve("artifacts.xml");
            log.info(String.format("Fetching %s", artifactsUri));
            Artifacts artifactsType = (Artifacts) unmarshaller.unmarshal(artifactsUri.toURL());

            for (ArtifactType artifact : artifactsType.getArtifact()) {
                URI artifactUri = rootUri.resolve(artifact.getFilename());
                log.info(String.format("Fetching %s", artifactUri));
                unpackContainer(ASIC_READER_FACTORY.open(artifactUri.toURL().openStream()), artifact.getFilename());
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
