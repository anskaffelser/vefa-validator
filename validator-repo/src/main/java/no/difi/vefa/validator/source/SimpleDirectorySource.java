package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.lang.ValidatorException;

import java.nio.file.Path;

/**
 * Defines a directory as source for validation artifacts. This version does not look for artifacts.xml
 */
public class SimpleDirectorySource extends AbstractSource {

    private Path[] directories;

    /**
     * Initiate the new source.
     *
     * @param directories Directory containing validation artifacts.
     */
    public SimpleDirectorySource(Path... directories) {
        this.directories = directories;
    }

    @Override
    public SourceInstance createInstance(Properties properties) throws ValidatorException {
        return new SimpleDirectorySourceInstance(properties, directories);
    }
}
