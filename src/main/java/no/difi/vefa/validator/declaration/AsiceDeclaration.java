package no.difi.vefa.validator.declaration;

import com.google.common.io.ByteStreams;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.CachedFile;
import no.difi.vefa.validator.api.DeclarationWithChildren;
import no.difi.vefa.validator.api.DeclarationWithConverter;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.lang.ValidatorException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Type("zip.asice")
public class AsiceDeclaration extends AbstractXmlDeclaration
        implements DeclarationWithChildren, DeclarationWithConverter {

    private static final String MIME = "application/vnd.etsi.asic-e+zip";

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        if (content[28] != 0)
            return false;

        try {
            ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(content));
            ZipEntry entry = zipInputStream.getNextEntry();

            if ("mimetype".equals(entry.getName()))
                return MIME.equals(new String(ByteStreams.toByteArray(zipInputStream)));
        } catch (IOException e) {
            // No action.
        }

        return false;
    }

    @Override
    public List<String> detect(InputStream contentStream, List<String> parent) {
        return Collections.singletonList(MIME);
    }

    @Override
    public Expectation expectations(byte[] content) {
        return null;
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException {
        try {
            ByteStreams.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new ValidatorException(e.getMessage(), e);
        }
    }

    @Override
    public Iterable<CachedFile> children(InputStream inputStream) {
        try {
            AsicReader asicReader = AsicReaderFactory.newFactory().open(inputStream);
            List<CachedFile> files = new ArrayList<>();

            String filename;
            while ((filename = asicReader.getNextFile()) != null) {
                files.add(CachedFile.of(filename, ByteStreams.toByteArray(asicReader.inputStream())));
            }

            return files;
        } catch (IOException e) {
            return null;
        }
    }
}
