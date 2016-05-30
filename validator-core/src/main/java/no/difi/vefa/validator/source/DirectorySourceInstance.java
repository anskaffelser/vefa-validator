package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

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
    public DirectorySourceInstance(Properties properties, Set<String> capabilities, Path... directories) throws ValidatorException {
        // Call #AbstractSourceInstance().
        super(properties);

        try {
            for (Path directory : directories) {
                logger.info("Directory: {}", directory);

                // Directories containing artifacts.xml results in lower memory footprint.
                if (Files.exists(directory.resolve("artifacts.xml"))) {
                    // Create unmarshaller (XML => Java)
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                    // Read artifacts.xml
                    Path artifactsPath = directory.resolve("artifacts.xml");
                    logger.info("Loading {}", artifactsPath);
                    Artifacts artifactsType = (Artifacts) unmarshaller.unmarshal(Files.newInputStream(artifactsPath));

                    // Loop through artifacts.
                    for (ArtifactType artifact : artifactsType.getArtifact()) {
                        boolean loadArtifact = true;

                        if (artifact.getCapabilities() != null)
                            for (String capability : artifact.getCapabilities().split(","))
                                if (capabilities.contains(capability))
                                    loadArtifact = false;

                        if (loadArtifact) {
                            // Load validation artifact to memory.
                            Path artifactPath = directory.resolve(artifact.getFilename());
                            logger.info("Loading {}", artifactPath);
                            unpackContainer(asicReaderFactory.open(artifactPath), artifact.getFilename());
                        }
                    }
                } else {
                    // Detect all ASiC-E-files in the directory.
                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                        for (Path path : directoryStream) {
                            if (path.toString().endsWith(".asice")) {
                                logger.info("Loading: {}", path);
                                unpackContainer(asicReaderFactory.open(path), path.getFileName().toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log and throw ValidatorException.
            logger.warn(e.getMessage());
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
