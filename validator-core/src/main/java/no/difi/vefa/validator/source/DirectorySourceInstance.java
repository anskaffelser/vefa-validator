package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Defines a directory as source for validation artifacts.
 */
class DirectorySourceInstance extends AbstractSourceInstance {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(DirectorySourceInstance.class);

    /**
     * Constructor, loads validation artifacts into memory.
     *
     * @param directories Directories containing validation artifacts.
     * @throws ValidatorException
     */
    public DirectorySourceInstance(Properties properties, Path... directories) throws ValidatorException {
        // Call #AbstractSourceInstance().
        super(properties);

        for (Path directory : directories) {
            logger.info(String.format("Directory: %s", directory));

            try {
                // Directories containing artifacts.xml results in lower memory footprint.
                if (Files.exists(directory.resolve("artifacts.xml"))) {
                    // Create unmarshaller (XML => Java)
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                    // Read artifacts.xml
                    Path artifactsPath = directory.resolve("artifacts.xml");
                    logger.info(String.format("Loading %s", artifactsPath));
                    Artifacts artifactsType = (Artifacts) unmarshaller.unmarshal(Files.newInputStream(artifactsPath));

                    // Loop through artifacts.
                    for (ArtifactType artifact : artifactsType.getArtifact()) {
                        // Load validation artifact to memory.
                        Path artifactPath = directory.resolve(artifact.getFilename());
                        logger.info(String.format("Loading %s", artifactPath));
                        unpackContainer(asicReaderFactory.open(artifactPath), artifact.getFilename());
                    }
                } else {
                    // Detect all ASiC-E-files in the directory.
                    for (File file : FileUtils.listFiles(directory.toFile(), new RegexFileFilter(".*\\.asice"), TrueFileFilter.INSTANCE)) {
                        // Load validation artifact to memory.
                        logger.info(String.format("Loading: %s", file));
                        unpackContainer(asicReaderFactory.open(file), file.getName());
                    }
                }
            } catch (Exception e) {
                // Log and throw ValidatorException.
                logger.warn(e.getMessage());
                throw new ValidatorException(e.getMessage(), e);
            }
        }
    }
}
