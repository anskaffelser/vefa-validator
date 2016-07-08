package no.difi.vefa.validator;

import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.source.SimpleDirectorySource;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import no.difi.xsd.vefa.validator._1.Configurations;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This class is a Cli for generation of artifacts.xml in a repo of validation artifacts.
 */
public class Repo {

    /**
     * Context for JAXB
     */
    private static JAXBContext jaxbContext = JAXBHelper.context(Configurations.class, Artifacts.class);

    /**
     * Generates list of artifacts for a given directory.
     *
     * @param directory   Directory for repository.
     * @param writeToDisk Set to true if list of artifacts is to be written do disk.
     * @return List of current artifacts.
     * @throws Exception
     */
    public static Artifacts generateArtifacts(Path directory, boolean writeToDisk) throws Exception {
        // Use a regular validator engine to load all artifacts in repo and calculate current configuration.
        SourceInstance sourceInstance = new SimpleDirectorySource(directory).createInstance(ValidatorDefaults.PROPERTIES);

        // Holds the list of detected artifacts.
        final List<ArtifactType> artifactsTypes = new ArrayList<>();

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

                        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                        // If inspected configuration file is part of the set of detected files.
                        // if (fileFound.contains(parentString)) {
                            // Read configuration file.
                            Configurations configurations = unmarshaller.unmarshal(new StreamSource(Files.newInputStream(file)), Configurations.class).getValue();

                            // New artifact
                            ArtifactType artifactType = new ArtifactType();
                            // Name of artifact
                            artifactType.setName(configurations.getName());
                            // Detect filename from recorded name as metadata may not correlate with internal name.
                            artifactType.setFilename(parentString.substring(parentString.lastIndexOf("/") + 1));
                            // Fetch timestamp of creation.
                            artifactType.setTimestamp(configurations.getTimestamp());
                            // Add artifact to list of artifacts.
                            artifactsTypes.add(artifactType);
                        // }
                    } catch (JAXBException e) {
                        // We are only allowed to return IOException.
                        throw new IOException(e.getMessage(), e);
                    }
                }
                // Next file, please.
                return FileVisitResult.CONTINUE;
            }
        });

        // Order by name and timestamp
        Collections.sort(artifactsTypes, new Comparator<ArtifactType>() {
            @Override
            public int compare(ArtifactType o1, ArtifactType o2) {
                int n = o1.getName().compareTo(o2.getName());
                return n != 0 ? n : Long.compare(o2.getTimestamp(), o1.getTimestamp());
            }
        });

        // Holds the final list of artifacts.
        Artifacts artifacts = new Artifacts();

        // Copy to artifact manifest only the newest instance based on timestamp for a given named artifact.
        List<String> names = new ArrayList<>();
        for (ArtifactType artifactType : artifactsTypes) {
            if (!names.contains(artifactType.getName())) {
                artifacts.getArtifact().add(artifactType);
                names.add(artifactType.getName());
            }
        }

        // Set newest (highest) timestamp on artifacts-element.
        for (ArtifactType artifactType : artifacts.getArtifact())
            if (artifactType.getTimestamp() > artifacts.getTimestamp())
                artifacts.setTimestamp(artifactType.getTimestamp());

        // Save result to disk.
        if (writeToDisk) {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(artifacts, Files.newOutputStream(directory.resolve("artifacts.xml")));
        }

        // Return list of artifacts.
        return artifacts;
    }
}
