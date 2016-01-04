package no.difi.vefa.validator.api;

import java.io.IOException;
import java.nio.file.FileSystem;

/**
 * An instance representing a source.
 */
public interface SourceInstance {

    /**
     * Access to the filesystem delivered by the instance.
     *
     * @return Filesystem instance.
     */
    FileSystem getFileSystem();
    
    void close() throws IOException;

}
