package no.difi.vefa.validator.source;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.nio.file.Path;

/**
 * Defines a directory as source for validation artifacts.
 */
@Slf4j
class SimpleDirectorySourceInstance extends AbstractSourceInstance {

    /**
     * Constructor, loads validation artifacts into memory.
     *
     * @param directories Directories containing validation artifacts.
     * @throws ValidatorException
     */
    public SimpleDirectorySourceInstance(Properties properties, Path... directories) throws ValidatorException {
        // Call #AbstractSourceInstance().
        super(properties);

        for (Path directory : directories) {
            log.info(String.format("Directory: %s", directory));

            try {
                // Detect all ASiC-E-files in the directory.
                for (File file : FileUtils.listFiles(directory.toFile(), new RegexFileFilter(".*\\.asice"), TrueFileFilter.INSTANCE)) {
                    // Load validation artifact to memory.
                    log.info(String.format("Loading: %s", file));
                    unpackContainer(asicReaderFactory.open(file), file.getName());
                }
            } catch (Exception e) {
                // Log and throw ValidatorException.
                log.warn(e.getMessage());
                throw new ValidatorException(e.getMessage(), e);
            }
        }
    }
}
