package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.api.ValidatorException;

import java.nio.file.Path;

/**
 * Defines a directory as source for validation artifacts. This version does not look for artifacts.xml
 */
public class SimpleDirectorySource extends AbstractSource {

    private Path directory;

    /**
     * Initiate the new source.
     *
     * @param directory Directory containing validation artifacts.
     */
    public SimpleDirectorySource(Path directory) {
        this.directory = directory;
    }

    public SourceInstance createInstance() throws ValidatorException {
        return new SimpleDirectorySourceInstance(directory);
    }
}
