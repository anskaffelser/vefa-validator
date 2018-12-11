package no.difi.vefa.validator.source;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.asic.SignatureMethod;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.Artifacts;

import javax.xml.bind.JAXBContext;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
abstract class AbstractSourceInstance implements SourceInstance, Closeable {

    protected static AsicReaderFactory asicReaderFactory = AsicReaderFactory.newFactory(SignatureMethod.CAdES);
    protected static JAXBContext jaxbContext = JAXBHelper.context(Artifacts.class);

    protected FileSystem fileSystem;
    protected Properties properties;

    public AbstractSourceInstance(Properties properties) {
        this.properties = properties;

        try {
            fileSystem = Jimfs.newFileSystem(Configuration.unix());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create VEFA Validator filesystem.", e);
        }
    }

    protected void unpackContainer(AsicReader asicReader, String targetName) throws IOException {
        // Prepare copying from asice-file to in-memory filesystem
        Path targetDirectory = fileSystem.getPath(targetName);

        // Copy content
        String filename;
        while ((filename = asicReader.getNextFile()) != null) {
            Path outputPath = targetDirectory.resolve(filename);
            Files.createDirectories(outputPath.getParent());
            log.debug("{}", outputPath);

            asicReader.writeFile(outputPath);
        }

        // Close asice-file
        asicReader.close();

        // Listing signatures
        // for (Certificate certificate : asicReader.getAsicManifest().getCertificate())
        //     log.info("Signed by '{}'", certificate.getSubject());

        // TODO Validate certificate?
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public List<String> getConfigs() {
        return null;
    }

    @Override
    public void close() throws IOException {
        if (fileSystem != null) {
            fileSystem.close();
            fileSystem = null;
        }
    }
}
