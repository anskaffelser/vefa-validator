package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.lang.ValidatorException;

import java.nio.file.Path;

/**
 * Defines a directories as source for validation artifacts.
 */
public class DirectorySource implements Source {

    private final Path[] directories;

    /**
     * Initiate the new source.
     *
     * @param directories Directories containing validation artifacts.
     */
    public DirectorySource(Path... directories) {
        this.directories = directories;
    }

    @Override
    public SourceInstance createInstance() throws ValidatorException {
        return new DirectorySourceInstance(directories);
    }
}
