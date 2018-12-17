package no.difi.vefa.validator.util;

import org.w3c.dom.ls.LSInput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathLSInput implements LSInput {

    private String publicId;

    private String sysId;

    private Path file;

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public PathLSInput(Path file, String publicId, String sysId) {
        this.publicId = publicId;
        this.sysId = sysId;
        this.file = file;

        try {
            Files.copy(file, outputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
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
        return null;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        // No action
    }

    @Override
    public String getStringData() {
        return outputStream.toString();
    }

    @Override
    public void setStringData(String stringData) {
        // No action
    }

    @Override
    public String getSystemId() {
        return sysId;
    }

    @Override
    public void setSystemId(String systemId) {
        // No action
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        // No action
    }

    @Override
    public String getBaseURI() {
        return file.toString();
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