package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.vefa.validator.api.DeclarationWithChildren;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AsiceDeclaration implements DeclarationWithChildren {

    private byte[] startsWith = new byte[]{0x50, 0x4B, 0x03, 0x04};

    @Override
    public boolean verify(byte[] content) throws ValidatorException {
        if (!Arrays.equals(startsWith, Arrays.copyOfRange(content, 0, 4)))
            return false;

        if (content[28] != 0)
            return false;

        try {
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(content));
            ZipEntry entry = zipInputStream.getNextEntry();

            if ("mimetype".equals(entry.getName())) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ByteStreams.copy(zipInputStream, byteArrayOutputStream);

                return "application/vnd.etsi.asic-e+zip".equals(byteArrayOutputStream.toString());
            }
        } catch (IOException e) {
            // No action.
        }

        return false;
    }

    @Override
    public String detect(byte[] content) throws ValidatorException {
        return "ASiC-E";
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }

    @Override
    public Iterable<InputStream> children(InputStream inputStream) {
        try {
            return new AsicIterator(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    private class AsicIterator implements Iterator<InputStream>, Iterable<InputStream> {

        private AsicReader asicReader;

        public AsicIterator(InputStream inputStream) throws IOException {
            asicReader  = AsicReaderFactory.newFactory().open(inputStream);
        }

        @Override
        public Iterator<InputStream> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            try {
                return asicReader.getNextFile() != null;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public InputStream next() {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                asicReader.writeFile(byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void remove() {
            // No action.
        }
    }
}
