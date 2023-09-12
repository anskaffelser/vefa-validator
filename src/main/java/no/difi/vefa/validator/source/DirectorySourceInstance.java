package no.difi.vefa.validator.source;

import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Defines a directory as source for validation artifacts.
 */
@Slf4j
class DirectorySourceInstance extends AbstractSourceInstance {

    /**
     * Constructor, loads validation artifacts into memory.
     *
     * @param directories Directories containing validation artifacts.
     */
    public DirectorySourceInstance(Path... directories) throws ValidatorException {
        try {
            for (Path directory : directories) {
                log.info("Directory: {}", directory);

                // Directories containing artifacts.xml results in lower memory footprint.
                if (Files.exists(directory.resolve("artifacts.xml"))) {
                    // Create unmarshaller (XML => Java)
                    Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

                    // Read artifacts.xml
                    Path artifactsPath = directory.resolve("artifacts.xml");
                    log.info("Loading {}", artifactsPath);
                    Artifacts artifactsType;

                    try (InputStream inputStream = Files.newInputStream(artifactsPath)) {
                        artifactsType = unmarshaller
                                .unmarshal(new StreamSource(inputStream), Artifacts.class).getValue();
                    }

                    // Loop through artifacts.
                    for (ArtifactType artifact : artifactsType.getArtifact()) {
                        // Load validation artifact to memory.
                        Path artifactPath = directory.resolve(artifact.getFilename());
                        log.info("Loading {}", artifactPath);
                        try (InputStream inputStream = Files.newInputStream(artifactPath)) {
                            unpackContainer(inputStream, artifact.getFilename());
                        }
                    }
                } else {
                    // Detect all ASiC-E-files in the directory.
                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
                        for (Path path : directoryStream) {
                            if (path.toString().endsWith(".asice") || path.toString().endsWith(".zip")) {
                                log.info("Loading: {}", path);
                                try (InputStream inputStream = Files.newInputStream(path)) {
                                    unpackContainer(inputStream, path.getFileName().toString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log and throw ValidatorException.
            log.warn(e.getMessage());
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
