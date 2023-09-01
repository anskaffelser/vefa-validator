package no.difi.vefa.validator.util;

import org.w3c.dom.ls.LSInput;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

public class HolderLSInput implements LSInput {

    private final String file;

    private final byte[] content;

    public HolderLSInput(byte[] content, String path) {
        this.content = content;
        this.file = path;
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        // No action
    }

    @Override
    public InputStream getByteStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        // No action
    }

    @Override
    public String getStringData() {
        return new String(content);
    }

    @Override
    public void setStringData(String stringData) {
        // No action
    }

    @Override
    public String getSystemId() {
        return String.format("holder:%s", file);
    }

    @Override
    public void setSystemId(String systemId) {
        // No action
    }

    @Override
    public String getPublicId() {
        return String.format("holder:%s", file);
    }

    @Override
    public void setPublicId(String publicId) {
        // No action
    }

    @Override
    public String getBaseURI() {
        return file;
    }

    @Override
    public void setBaseURI(String baseURI) {
        // No action
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public void setEncoding(String encoding) {
        // No action
    }

    @Override
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        // No action
    }
}