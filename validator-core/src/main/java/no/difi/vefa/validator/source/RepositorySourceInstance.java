package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import java.net.URI;

class RepositorySourceInstance extends AbstractSourceInstance {

    private static Logger logger = LoggerFactory.getLogger(RepositorySourceInstance.class);

    public RepositorySourceInstance(Properties properties, URI rootUri) throws ValidatorException {
        super(properties);

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            URI artifactsUri = rootUri.resolve("artifacts.xml");
            logger.info(String.format("Fetching %s", artifactsUri));
            Artifacts artifactsType = (Artifacts) unmarshaller.unmarshal(artifactsUri.toURL());

            for (ArtifactType artifact : artifactsType.getArtifact()) {
                URI artifactUri = rootUri.resolve(artifact.getFilename());
                logger.info(String.format("Fetching %s", artifactUri));
                unpackContainer(asicReaderFactory.open(artifactUri.toURL().openStream()), artifact.getFilename());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
