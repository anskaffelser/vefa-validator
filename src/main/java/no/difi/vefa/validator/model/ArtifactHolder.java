package no.difi.vefa.validator.model;

import com.google.common.io.ByteStreams;

import javax.xml.transform.stream.StreamSource;
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
public class ArtifactHolder {

    public static ArtifactHolder of(Map<String, byte[]> content) {
        return new ArtifactHolder(content);
    }

    public static ArtifactHolder of(InputStream inputStream) throws IOException {
        Map<String, byte[]> content = new HashMap<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                content.put(zipEntry.getName(), ByteStreams.toByteArray(zipInputStream));
                zipInputStream.closeEntry();
            }
        }

        return of(content);
    }

    private final Map<String, byte[]> content;

    private ArtifactHolder(Map<String, byte[]> content) {
        this.content = content;
    }

    public byte[] get(String path) {
        return content.get(path);
    }

    public InputStream getInputStream(String path) throws IOException {
        if (!content.containsKey(path))
            throw new IOException(String.format("Resource not found in holder: %s", path));

        return new ByteArrayInputStream(content.get(path));
    }

    public StreamSource getStream(String path) throws IOException {
        return new StreamSource(getInputStream(path), "holder:" + path);
    }

    public Document getDocument(String path) throws IOException {
        if (!content.containsKey(path))
            throw new IOException(String.format("Resource not found in holder: %s", path));

        return Document.of(content.get(path));
    }

    public Set<String> getFilenames() {
        return content.keySet();
    }

}
