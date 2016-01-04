package no.difi.vefa.validator.source;

import com.google.common.collect.ImmutableMap;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.JimfsFileSystemProvider;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.asic.SignatureMethod;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.xsd.asic.model._1.Certificate;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;

abstract class AbstractSourceInstance implements SourceInstance {

    private static Logger logger = LoggerFactory.getLogger(AbstractSourceInstance.class);

    protected static AsicReaderFactory asicReaderFactory = AsicReaderFactory.newFactory(SignatureMethod.CAdES);

    protected static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Artifacts.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected FileSystem fileSystem;

    protected Properties properties;

    public AbstractSourceInstance(Properties properties) {
        this.properties = properties;

        List<FileSystemProvider> list = JimfsFileSystemProvider.installedProviders();
        try {
            URI uri = new URI("jimfs", "vefa", null, null);
            ImmutableMap<String, ?> env = ImmutableMap.of("config", Configuration.unix());

            for (FileSystemProvider provider : list)
                if (provider instanceof JimfsFileSystemProvider)
                    fileSystem = provider.newFileSystem(uri, env);

            if (fileSystem == null)
                fileSystem = new JimfsFileSystemProvider().newFileSystem(uri, env);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create VEFA Validator instance", e);
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
            logger.debug("{}", outputPath);

            asicReader.writeFile(outputPath);
        }

        // Close asice-file
        asicReader.close();

        // Listing signatures
        for (Certificate certificate : asicReader.getAsicManifest().getCertificates())
            logger.info(String.format("Signature: %s", certificate.getSubject()));

        // TODO Validate certificate?
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }
    
    @Override
    public void close() throws IOException {
        if (fileSystem != null) {
            fileSystem.close();
            fileSystem = null;
        }
    }
}
