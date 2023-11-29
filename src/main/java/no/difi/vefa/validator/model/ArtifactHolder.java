package no.difi.vefa.validator.model;

import com.google.common.io.ByteStreams;
import jakarta.xml.bind.JAXBContext;
import lombok.Getter;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.JaxbUtils;
import no.difi.xsd.vefa.validator._1.Configurations;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author erlend
 */
public class ArtifactHolder {

    private static final JAXBContext JAXB = JaxbUtils.context(Configurations.class);

    public static ArtifactHolder of(ArtifactInfo info, InputStream inputStream) throws IOException {
        Map<String, byte[]> content = new HashMap<>();

        String configFilename = null;

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                var filename = zipEntry.getName();

                // Load content to
                content.put(filename, ByteStreams.toByteArray(zipInputStream));
                zipInputStream.closeEntry();

                // Remember configuration filename
                if (filename.startsWith("config") && filename.endsWith(".xml")) {
                    configFilename = filename;
                }
            }
        }

        if (Objects.isNull(configFilename))
            throw new IOException("Configuration not found.");

        try {
            return new ArtifactHolder(content, Document.of(content.get(configFilename)).unmarshal(JAXB, Configurations.class));
        } catch (ValidatorException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    private final Map<String, byte[]> content;

    @Getter
    private final Configurations configurations;

    private ArtifactHolder(Map<String, byte[]> content, Configurations configurations) {
        this.content = content;
        this.configurations = configurations;
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
