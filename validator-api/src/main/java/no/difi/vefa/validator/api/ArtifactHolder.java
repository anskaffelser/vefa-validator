package no.difi.vefa.validator.api;

import java.io.InputStream;
import java.util.Set;

/**
 * @author erlend
 */
public interface ArtifactHolder {

    boolean exists(String path);

    byte[] get(String path);

    InputStream getInputStream(String path);

    Set<String> getFilenames();

}
