package no.difi.vefa.validator.util;

import org.apache.commons.io.IOUtils;
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

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public PathLSInput(Path file, String publicId, String sysId) {
        this.publicId = publicId;
        this.sysId = sysId;
        this.file = file;

        try {
            InputStream inputStream = Files.newInputStream(file);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {

    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    public void setByteStream(InputStream byteStream) {

    }

    @Override
    public String getStringData() {
        return outputStream.toString();
    }

    @Override
    public void setStringData(String stringData) {

    }

    @Override
    public String getSystemId() {
        return sysId;
    }

    @Override
    public void setSystemId(String systemId) {

    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {

    }

    @Override
    public String getBaseURI() {
        return file.toString();
    }

    @Override
    public void setBaseURI(String baseURI) {

    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public void setEncoding(String encoding) {

    }

    @Override
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {

    }
}