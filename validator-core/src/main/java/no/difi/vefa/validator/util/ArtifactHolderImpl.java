package no.difi.vefa.validator.util;

import com.google.common.io.ByteStreams;
import no.difi.asic.AsicReader;
import no.difi.vefa.validator.api.ArtifactHolder;

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
public class ArtifactHolderImpl implements ArtifactHolder {

    private Map<String, byte[]> content;

    public static ArtifactHolder load(AsicReader asicReader) throws IOException {
        Map<String, byte[]> content = new HashMap<>();

        String filename;
        while ((filename = asicReader.getNextFile()) != null) {
            content.put(filename, ByteStreams.toByteArray(asicReader.inputStream()));
        }

        // Close asice-file
        asicReader.close();

        return new ArtifactHolderImpl(content);
    }

    public static ArtifactHolder load(InputStream inputStream) throws IOException {
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

    private ArtifactHolderImpl(Map<String, byte[]> content) {
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
