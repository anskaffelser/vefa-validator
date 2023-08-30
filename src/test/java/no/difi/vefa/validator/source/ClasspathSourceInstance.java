package no.difi.vefa.validator.source;

import jakarta.xml.bind.Unmarshaller;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

class ClasspathSourceInstance extends AbstractSourceInstance {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathSourceInstance.class);

    public ClasspathSourceInstance(Properties properties, String location) throws ValidatorException {
        super(properties);

        String artifactsUri = location + "artifacts.xml";

        try (InputStream inputStream = getClass().getResourceAsStream(artifactsUri)) {
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

            logger.info(String.format("Fetching %s", artifactsUri));
            unpack(location, unmarshaller.unmarshal(new StreamSource(inputStream), Artifacts.class).getValue());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    private void unpack(String location, Artifacts artifactsType) throws IOException {
        for (ArtifactType artifact : artifactsType.getArtifact()) {
            String artifactUri = location + artifact.getFilename();
            logger.info(String.format("Fetching %s", artifactUri));
            try (InputStream inputStream = getClass().getResourceAsStream(artifactUri)) {
                unpackContainer(inputStream, artifact.getFilename());
            }
        }
    }
}
