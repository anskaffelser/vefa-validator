package no.difi.vefa.validator.api;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author erlend
 */
public interface ArtifactHolder {

    boolean exists(String path);

    byte[] get(String path);

    InputStream getInputStream(String path);

    Set<String> getFilenames();

    static ArtifactHolder of(InputStream inputStream) throws IOException {
        Map<String, byte[]> content = new HashMap<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                content.put(zipEntry.getName(), ByteStreams.toByteArray(zipInputStream));
                zipInputStream.closeEntry();
            }
        }

        return new ArtifactHolderImpl(content);
    }

    class ArtifactHolderImpl implements ArtifactHolder {

        private final Map<String, byte[]> content;

        public ArtifactHolderImpl(Map<String, byte[]> content) {
            this.content = content;
        }

        @Override
        public boolean exists(String path) {
            return content.containsKey(path);
        }

        @Override
        public byte[] get(String path) {
            return content.get(path);
        }

        @Override
        public InputStream getInputStream(String path) {
            return new ByteArrayInputStream(content.get(path));
        }

        @Override
        public Set<String> getFilenames() {
            return content.keySet();
        }
    }
}
