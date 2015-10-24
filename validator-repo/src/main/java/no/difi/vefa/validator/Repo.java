package no.difi.vefa.validator;

import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.source.SimpleDirectorySource;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import no.difi.xsd.vefa.validator._1.Configurations;
import no.difi.xsd.vefa.validator._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is a Cli for generation of artifacts.xml in a repo of validation artifacts.
 */
public class Repo {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(Repo.class);

    /**
     * Unmarshaller (XML => Java)
     */
    private static Unmarshaller unmarshaller;

    /**
     * Marshaller (Java => XML)
     */
    private static Marshaller marshaller;

    /**
     * Load JAXBContext used.
     */
    static {
        try {
            // Context
            JAXBContext jaxbContext = JAXBContext.newInstance(Configurations.class, Artifacts.class);

            // Unmarshaller
            unmarshaller = jaxbContext.createUnmarshaller();

            // Marshaller
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (JAXBException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Generates list of artifacts for a given directory.
     *
     * @param directory Directory for repository.
     * @param writeToDisk Set to true if list of artifacts is to be written do disk.
     * @return List of current artifacts.
     * @throws Exception
     */
    public static Artifacts generateArtifacts(Path directory, boolean writeToDisk) throws Exception {
        // Use a regular validator engine to load all artifacts in repo and calculate current configuration.
        SourceInstance sourceInstance = new SimpleDirectorySource(directory).createInstance(ValidatorDefaults.PROPERTIES);
        ValidatorEngine validatorEngine = new ValidatorEngine(sourceInstance);

        // Holds files associated with current configuration.
        final Set<String> fileFound = new TreeSet<>();
        // Holds configurations referenced but not found.
        Set<String> unavailable = new TreeSet<>();

        // Run through all valid declarations in the validator engine.
        for (String declaration : validatorEngine.getDeclarations()) {
            // Fetch configuration
            Configuration configuration = new Configuration(validatorEngine.getConfiguration(declaration));
            // Normalize it to detect resources used and referenced.
            configuration.normalize(validatorEngine);

            // Fetch source file from filename in files used for validation.
            for (FileType fileType : configuration.getFile())
                fileFound.add(fileType.getPath().split("#")[0]);
            // Fetch source file for filename in stylesheet.
            if (configuration.getStylesheet() != null)
                fileFound.add(configuration.getStylesheet().getPath().split("#")[0]);

            // Collect referenced configurations not detected in repository.
            for (String notLoaded : configuration.getNotLoaded())
                unavailable.add(notLoaded);
        }

        // Holds the final list of artifacts.
        final Artifacts artifacts = new Artifacts();

        // Matcher used to find configuration files.
        final PathMatcher matcher = sourceInstance.getFileSystem().getPathMatcher("glob:**/config*.xml");

        // Loop through files in the virtual filesystem.
        Files.walkFileTree(sourceInstance.getFileSystem().getPath("/"), new HashSet<FileVisitOption>(), 3, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // If file is a configuration file...
                if (matcher.matches(file)) {
                    try {
                        String parentString = file.getParent().toString();
                        // If inspected configuration file is part of the set of detected files.
                        if (fileFound.contains(parentString)) {
                            // Read configuration file.
                            Configurations configurations = ((Configurations) unmarshaller.unmarshal(Files.newInputStream(file)));

                            // New artifact
                            ArtifactType artifactType = new ArtifactType();
                            // Detect filename from recorded name as metadata may not correlate with internal name.
                            artifactType.setFilename(parentString.substring(parentString.lastIndexOf("/") + 1));
                            // Fetch timestamp of creation.
                            artifactType.setTimestamp(configurations.getTimestamp());
                            // Add artifact to list of artifacts.
                            artifacts.getArtifact().add(artifactType);
                        }
                    } catch (JAXBException e) {
                        // We are only allowed to return IOException.
                        throw new IOException(e.getMessage(), e);
                    }
                }
                // Next file, please.
                return FileVisitResult.CONTINUE;
            }
        });

        // Set newest (highest) timestamp on artifacts-element.
        for (ArtifactType artifactType : artifacts.getArtifact())
            if (artifactType.getTimestamp() > artifacts.getTimestamp())
                artifacts.setTimestamp(artifactType.getTimestamp());

        // Simply list configurations detected as not loaded by validator.
        for (String notLoaded : unavailable)
            logger.warn(String.format("Not found: %s", notLoaded));

        // Save result to disk.
        if (writeToDisk)
            marshaller.marshal(artifacts, Files.newOutputStream(directory.resolve("artifacts.xml")));

        // Return list of artifacts.
        return artifacts;
    }
}
