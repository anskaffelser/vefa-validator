package no.difi.vefa.validator.api;

import java.nio.file.FileSystem;

/**
 * An instance representing a source.
 * <p/>
 * Implementations in need of close() method should implement java.io.Closeable.
 */
public interface SourceInstance {

    /**
     * Access to the filesystem delivered by the instance.
     *
     * @return Filesystem instance.
     */
    FileSystem getFileSystem();

}
