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

    private byte[] content;

    public CachedFile(byte[] content) {
        this.content = content;
    }

    public CachedFile(String filename, byte[] content) {
        this(content);
        this.filename = filename;
    }

    public InputStream getContentStream() {
        return new ByteArrayInputStream(content);
    }
}
