package no.difi.vefa.validator;

import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.source.DirectorySource;
import no.difi.xsd.vefa.validator._1.*;
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

public class Repo {

    private static Logger logger = LoggerFactory.getLogger(Repo.class);

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Configurations.class, Artifacts.class);
        } catch (JAXBException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void main(String... args) throws Exception {
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        final Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        for (String directory : args) {
            Path workDirectory = Paths.get(directory);
            SourceInstance sourceInstance = new DirectorySource(workDirectory).createInstance();
            ValidatorEngine validatorEngine = new ValidatorEngine(sourceInstance);

            Set<String> fileFound = new TreeSet<>();
            Set<String> unavailable = new TreeSet<>();

            for (DocumentDeclaration declaration : validatorEngine.getDeclarations()) {
                Configuration configuration = new Configuration(validatorEngine.getConfiguration(declaration));
                configuration.normalize(validatorEngine);

                for (FileType fileType : configuration.getFile())
                    fileFound.add(fileType.getPath().split("#")[0]);
                if (configuration.getStylesheet() != null)
                    fileFound.add(configuration.getStylesheet().getPath().split("#")[0]);

                for (String notLoaded : configuration.getNotLoaded())
                    unavailable.add(notLoaded);
            }

            Artifacts artifacts = new Artifacts();

            for (String file : fileFound) {
                final ArtifactType artifactType = new ArtifactType();
                artifactType.setFilename(file.substring(file.lastIndexOf("/") + 1));

                // Find timestamp...
                final PathMatcher matcher = sourceInstance.getFileSystem().getPathMatcher("glob:**/config*.xml");

                Files.walkFileTree(sourceInstance.getFileSystem().getPath(artifactType.getFilename()), new HashSet<FileVisitOption>(), 3, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (matcher.matches(file)) {
                            try {
                                artifactType.setTimestamp(((Configurations) unmarshaller.unmarshal(Files.newInputStream(file))).getTimestamp());
                            } catch (JAXBException e) {
                                logger.warn(e.getMessage(), e);
                            }
                            return FileVisitResult.TERMINATE;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });

                artifacts.getArtifact().add(artifactType);

                logger.info(String.format("Found: %s", artifactType.getFilename()));
            }

            marshaller.marshal(artifacts, Files.newOutputStream(workDirectory.resolve("artifacts.xml")));

            for (String notLoaded : unavailable) {
                logger.warn(String.format("Not found: %s", notLoaded));
            }
        }
    }

}
