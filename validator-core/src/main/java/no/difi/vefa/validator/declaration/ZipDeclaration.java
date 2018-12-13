package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.vefa.validator.api.*;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@MetaInfServices(Declaration.class)
public class ZipDeclaration implements Declaration, DeclarationWithChildren {

    private static final byte[] startsWith = new byte[]{0x50, 0x4B, 0x03, 0x04};

    @Override
    public boolean verify(byte[] content, String parent) throws ValidatorException {
        return Arrays.equals(startsWith, Arrays.copyOfRange(content, 0, 4));
    }

    @Override
    public String detect(byte[] content, String parent) throws ValidatorException {
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
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }


    @Override
    public Iterable<InputStream> children(InputStream inputStream) {
        try {
            return new zipIterator(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    private class zipIterator implements IndexedIterator<InputStream>, Iterable<InputStream> {

        private ZipInputStream zipInputStream;
        private ZipEntry zipEntry;

        public zipIterator(InputStream inputStream) throws IOException {
            zipInputStream = new ZipInputStream(inputStream);
        }

        @Override
        public Iterator<InputStream> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            try {
                return (zipEntry = zipInputStream.getNextEntry()) != null;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public InputStream next() {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteStreams.copy(zipInputStream, byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void remove() {
            // No action.
        }

        @Override
        public String currentIndex() {
            return zipEntry.getName();
        }
    }
}
