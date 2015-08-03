package no.difi.vefa.validator;

import no.difi.vefa.validator.api.SourceInstance;
import no.difi.xsd.vefa.validator._1.ConfigurationType;
import no.difi.xsd.vefa.validator._1.Configurations;
import no.difi.xsd.vefa.validator._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

class ValidatorEngine {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(ValidatorEngine.class);

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Configurations.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to initiate configuration loader.", e);
        }
    }

    private Map<String, Path> configurationSourceMap = new HashMap<>();

    private Map<String, ConfigurationType> identifierMap = new HashMap<>();
    private Map<DocumentDeclaration, ConfigurationType> declarationMap = new HashMap<>();

    private List<String> packages = new ArrayList<>();

    /**
     * Loading a new validator engine loading configurations from current source.
     */
    ValidatorEngine(SourceInstance sourceInstance) throws ValidatorException {
        // Matcher to find configuration files.
        final PathMatcher matcher = sourceInstance.getFileSystem().getPathMatcher("glob:**/config*.xml");

        try {
            Files.walkFileTree(sourceInstance.getFileSystem().getPath("/"), new HashSet<FileVisitOption>(), 3, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (matcher.matches(file)) {
                        String configurationSource = addConfigurationSource(file.getParent());
                        try {
                            loadConfigurations(configurationSource, Files.newInputStream(file));
                        } catch (FileNotFoundException e) {
                            logger.warn(String.format("Unable to load configuration in file '%s'.", String.valueOf(file)));
                        } catch (ValidatorException e) {
                            throw new IOException(e.getMessage(), e);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    void loadConfigurations(String configurationSource, InputStream inputStream) throws ValidatorException{
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            loadConfigurations(configurationSource, (Configurations) unmarshaller.unmarshal(inputStream));
        } catch (JAXBException e) {
            throw new ValidatorException("Unable to read configurations.", e);
        }
    }

    void loadConfigurations(String configurationSource, Configurations configurations) {
        packages.addAll(configurations.getPackage());
        Collections.sort(packages);
        for (String pkg : configurations.getPackage())
            logger.info(String.format("Loaded: %s", pkg));

        for (ConfigurationType configuration : configurations.getConfiguration()) {
            for (FileType fileType : configuration.getFile()) {
                fileType.setPath(String.format("%s#%s", configurationSource, fileType.getPath()));
                fileType.setConfiguration(configuration.getIdentifier());
                fileType.setBuild(configuration.getBuild());
            }

            if (configuration.getStylesheet() != null)
                configuration.getStylesheet().setPath(String.format("%s#%s", configurationSource, configuration.getStylesheet().getPath()));

            // Add by identifier if not registered or weight is higher
            if (!identifierMap.containsKey(configuration.getIdentifier()) || identifierMap.get(configuration.getIdentifier()).getWeight() < configuration.getWeight())
                identifierMap.put(configuration.getIdentifier(), configuration);

            if (configuration.getBuild() != null) {
                String identifierBuild = String.format("%s#%s", configuration.getIdentifier(), configuration.getBuild());
                if (!identifierMap.containsKey(identifierBuild) || identifierMap.get(identifierBuild).getWeight() < configuration.getWeight())
                    identifierMap.put(identifierBuild, configuration);
            }

            if (configuration.getProfileId() != null && configuration.getCustomizationId() != null) {
                DocumentDeclaration declaration = new DocumentDeclaration(configuration.getCustomizationId(), configuration.getProfileId());
                if (!declarationMap.containsKey(declaration) || declarationMap.get(declaration).getWeight() < configuration.getWeight())
                    declarationMap.put(declaration, configuration);
            }
        }
    }

    public ConfigurationType getConfiguration(String identifier) throws ValidatorException {
        // if (!identifierMap.containsKey(identifier))
        //    throw new ValidatorException(String.format("Configuration '%s' not found", identifier));

        return identifierMap.get(identifier);
    }

    public ConfigurationType getConfiguration(DocumentDeclaration declaration) throws ValidatorException {
        if (!declarationMap.containsKey(declaration))
            throw new ValidatorException(String.format("Configuration for '%s' not found", declaration));

        return declarationMap.get(declaration);
    }

    public List<String> getPackages() {
        return packages;
    }

    public List<DocumentDeclaration> getDeclarations() {
        return new ArrayList<>(declarationMap.keySet());
    }

    public String addConfigurationSource(Path source) {
        String identifier = source.toString();
        configurationSourceMap.put(identifier, source);
        return identifier;
    }

    public Path getResource(String resource) throws IOException {
        String[] parts = resource.split("#", 2);
        return configurationSourceMap.get(parts[0]).resolve(parts[1]);
    }
}
