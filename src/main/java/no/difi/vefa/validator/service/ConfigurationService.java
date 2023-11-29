package no.difi.vefa.validator.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.ArtifactHolder;
import no.difi.vefa.validator.model.ArtifactInfo;
import no.difi.xsd.vefa.validator._1.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Singleton
public class ConfigurationService {

    @Inject
    private RepositoryService repositoryService;

    private final Map<String, ArtifactHolder> content = new HashMap<>();

    private final Map<String, ConfigurationType> identifiers = new HashMap<>();

    private final Map<String, ConfigurationType> declarations = new HashMap<>();

    @Inject
    public void init() throws IOException {
        for (var artifact : repositoryService.getListing()) {
            load(artifact);
        }
    }

    public void load(ArtifactInfo artifact) throws IOException {
        try (var inputStream = repositoryService.fetch(artifact)) {
            var holder = ArtifactHolder.of(artifact, inputStream);
            var configurations = holder.getConfigurations();

            // Check if we already have a newer instance
            if (content.containsKey(configurations.getName()) && content.get(configurations.getName()).getConfigurations().getTimestamp() > configurations.getTimestamp())
                return;

            content.put(configurations.getName(), holder);

            // Write to log when loading new packages.
            for (PackageType pkg : configurations.getPackage())
                log.info("Loaded '{}'", pkg.getValue());

            for (ConfigurationType configuration : configurations.getConfiguration()) {
                // Prepare files
                for (FileType fileType : configuration.getFile()) {
                    if (fileType.getType() == null)
                        fileType.setType(fileType.getPath().endsWith(".xsd") ? "xml.xsd" : "xml.schematron.xslt");
                    fileType.setPath(String.format("%s#%s", configurations.getName(), fileType.getPath()));
                    fileType.setConfiguration(configuration.getIdentifier().getValue());
                    fileType.setBuild(configuration.getBuild());
                }

                // Add by identifier if not registered or weight is higher
                if (!identifiers.containsKey(configuration.getIdentifier().getValue()) ||
                        identifiers.get(configuration.getIdentifier().getValue()).getWeight() < configuration.getWeight())
                    identifiers.put(configuration.getIdentifier().getValue(), configuration);

                // Convert standardid and profileid/customizationid to declarations
                if (configuration.getDeclaration().isEmpty()) {
                    if (configuration.getStandardId() == null) {
                        if (configuration.getProfileId() != null && configuration.getCustomizationId() != null) {
                            DeclarationType declaration = new DeclarationType();
                            declaration.setType("xml.ubl");
                            declaration.setValue(configuration.getProfileId() + "#" + configuration.getCustomizationId());
                            configuration.getDeclaration().add(declaration);
                        }
                    } else {
                        DeclarationType declarationType = new DeclarationType();
                        declarationType.setType("xml");
                        declarationType.setValue(configuration.getStandardId());
                        configuration.getDeclaration().add(declarationType);
                    }
                }

                // Index declarations
                for (DeclarationType declaration : configuration.getDeclaration()) {
                    String identifier = String.format("%s::%s", declaration.getType(), declaration.getValue());
                    if (!declarations.containsKey(identifier) ||
                            declarations.get(identifier).getWeight() < configuration.getWeight())
                        declarations.put(identifier, configuration);
                }

                // Index the configuration itself
                declarations.put(String.format("configuration::%s", configuration.getIdentifier().getValue()), configuration);
            }
        }
    }

    /**
     * Fetch raw configuration by using identifier.
     *
     * @param identifier Configuration identifier
     * @return Configuration
     */
    public ConfigurationType getConfiguration(String identifier) {
        return identifiers.get(identifier);
    }

    /**
     * Fetch raw configuration by using document declaration.
     *
     * @param declaration Document declaration.
     * @return Configuration if found
     */
    public ConfigurationType getConfigurationByDeclaration(String declaration) {
        if (declarations.containsKey(declaration))
            return declarations.get(declaration);

        return null;
    }

    public ArtifactHolder getResource(String resource) {
        String[] parts = resource.split("#", 2);
        return content.get(parts[0]);
    }


    public boolean update() throws IOException, ValidatorException {
        if (!repositoryService.update())
            return false;

        // TODO Reindexing

        return true;
    }

    public List<PackageType> getPackages() {
        return content.entrySet().stream()
                .filter(c -> c.getKey().equals(c.getValue().getConfigurations().getName()))
                .map(Map.Entry::getValue)
                .map(ArtifactHolder::getConfigurations)
                .map(Configurations::getPackage)
                .flatMap(List::stream)
                .toList();
    }
}
