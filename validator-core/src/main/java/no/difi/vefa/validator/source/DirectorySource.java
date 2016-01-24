package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.api.SourceInstance;

import java.nio.file.Path;

/**
 * Defines a directories as source for validation artifacts.
 */
public class DirectorySource extends AbstractSource {

    private Path[] directories;

    /**
     * Initiate the new source.
     *
     * @param directories Directories containing validation artifacts.
     */
    public DirectorySource(Path... directories) {
        this.directories = directories;
    }

    @Override
    public SourceInstance createInstance(Properties properties) throws ValidatorException {
        return new DirectorySourceInstance(properties, directories);
    }
}
