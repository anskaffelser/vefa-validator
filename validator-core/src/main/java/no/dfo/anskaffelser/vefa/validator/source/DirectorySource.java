package no.dfo.anskaffelser.vefa.validator.source;

import no.dfo.anskaffelser.vefa.validator.api.Properties;
import no.dfo.anskaffelser.vefa.validator.api.SourceInstance;
import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

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
