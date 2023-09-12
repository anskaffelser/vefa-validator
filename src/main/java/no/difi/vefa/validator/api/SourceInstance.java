package no.difi.vefa.validator.api;

import no.difi.vefa.validator.model.ArtifactHolder;

import java.util.Map;

/**
 * An instance representing a source.
 * <p/>
 * Implementations in need of close() method should implement java.io.Closeable.
 */
public interface SourceInstance {

    Map<String, ArtifactHolder> getContent();

    ArtifactHolder getContent(String path);

}
