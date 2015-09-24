package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.api.SourceInstance;

import java.nio.file.Path;

/**
 * Defines a directory as source for validation artifacts.
 */
public class DirectorySource extends AbstractSource {

    private Path directory;

    /**
     * Initiate the new source.
     *
     * @param directory Directory containing validation artifacts.
     */
    public DirectorySource(Path directory) {
        this.directory = directory;
    }

    @Override
    public SourceInstance createInstance(Properties properties) throws ValidatorException {
        return new DirectorySourceInstance(properties, directory);
    }
}
