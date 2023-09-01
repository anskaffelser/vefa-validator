package no.difi.vefa.validator.api;

import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author erlend
 */
@Getter
public class CachedFile {

    private String filename;

    private final byte[] content;

    public static CachedFile of(byte[] content) {
        return new CachedFile(content);
    }

    public static CachedFile of(String filename, byte[] content) {
        return new CachedFile(filename, content);
    }

    private CachedFile(byte[] content) {
        this.content = content;
    }

    private CachedFile(String filename, byte[] content) {
        this(content);
        this.filename = filename;
    }

    public InputStream getContentStream() {
        return new ByteArrayInputStream(content);
    }
}
