package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

class ClasspathSourceInstance extends AbstractSourceInstance {

    private static Logger logger = LoggerFactory.getLogger(ClasspathSourceInstance.class);

    public ClasspathSourceInstance(Properties properties, String location) throws ValidatorException {
        super(properties);

        try {
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            String artifactsUri = location + "artifacts.xml";
            logger.info(String.format("Fetching %s", artifactsUri));
            Artifacts artifactsType = unmarshaller.unmarshal(new StreamSource(getClass().getResourceAsStream(artifactsUri)), Artifacts.class).getValue();

            for (ArtifactType artifact : artifactsType.getArtifact()) {
                String artifactUri = location + artifact.getFilename();
                logger.info(String.format("Fetching %s", artifactUri));
                unpackContainer(ASIC_READER_FACTORY.open(getClass().getResourceAsStream(artifactUri)), artifact.getFilename());
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
