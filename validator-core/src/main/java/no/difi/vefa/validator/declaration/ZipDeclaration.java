package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.*;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Type("zip")
@MetaInfServices(Declaration.class)
public class ZipDeclaration implements Declaration, DeclarationWithChildren {

    private static final byte[] startsWith = new byte[]{0x50, 0x4B, 0x03, 0x04};

    @Override
    public boolean verify(byte[] content, String parent) {
        return Arrays.equals(startsWith, Arrays.copyOfRange(content, 0, 4));
    }

    @Override
    public String detect(byte[] content, String parent) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(content));
            ZipEntry entry = zipInputStream.getNextEntry();

            if ("mimetype".equals(entry.getName())) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteStreams.copy(zipInputStream, byteArrayOutputStream);
                return byteArrayOutputStream.toString();
            }
        } catch (IOException e) {
            // No action
        }

        return "application/zip";
    }

    @Override
    public Expectation expectations(byte[] content) {
        return null;
    }

    @Override
    public Iterable<CachedFile> children(InputStream inputStream) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            List<CachedFile> files = new ArrayList<>();

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                files.add(new CachedFile(zipEntry.getName(), ByteStreams.toByteArray(zipInputStream)));
            }

            return files;
        } catch (IOException e) {
            return null;
        }
    }
}
