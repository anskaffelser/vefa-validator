package no.difi.vefa.validator.source;

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
class SimpleDirectorySourceInstance extends AbstractSourceInstance {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(SimpleDirectorySourceInstance.class);

    /**
     * Constructor, loads validation artifacts into memory.
     *
     * @param directory Directory containing validation artifacts.
     * @throws ValidatorException
     */
    public SimpleDirectorySourceInstance(Path directory) throws ValidatorException {
        // Call #AbstractSourceInstance().
        super();

        logger.info(String.format("Directory: %s", directory));

        try {
            // Detect all ASiC-E-files in the directory.
            for (File file : FileUtils.listFiles(directory.toFile(), new RegexFileFilter(".*\\.asice"), TrueFileFilter.INSTANCE)) {
                // Load validation artifact to memory.
                logger.info(String.format("Loading: %s", file));
                unpackContainer(asicReaderFactory.open(file), file.getName());
            }
        } catch (Exception e) {
            // Log and throw ValidatorException.
            logger.warn(e.getMessage());
            throw new ValidatorException(e.getMessage(), e);
        }
    }
}
