package no.difi.vefa.validator;

import com.google.common.collect.Lists;
import no.difi.xsd.vefa.validator._1.ConfigurationType;
import no.difi.xsd.vefa.validator._1.FileType;

import java.util.ArrayList;
import java.util.List;

/**
 * Configurations found in validation artifacts are updated to this kind of object.
 */
class Configuration extends ConfigurationType {

    /**
     * List of resources not found during normalization of object.
     */
    private final List<String> notLoaded = new ArrayList<>();

    /**
     * Create new configuration based on configuration from XML.
     *
     * @param configurationType Configuration from XML.
     */
    Configuration(ConfigurationType configurationType) {
        // Copy rule
        this.setIdentifier(configurationType.getIdentifier());
        this.setTitle(configurationType.getTitle());
        this.setStandardId(configurationType.getStandardId());
        this.setCustomizationId(configurationType.getCustomizationId());
        this.setProfileId(configurationType.getProfileId());
        this.setWeight(configurationType.getWeight());
        this.setBuild(configurationType.getBuild());
        this.inherit = configurationType.getInherit();
        this.file = configurationType.getFile();
    }

    /**
     * Validation artifacts supports inheritance. This methods uses inheritance references to generate
     * a complete object containing all resources for a given document type.
     *
     * @param engine ValidatiorEngine for fetching of other configurations.
     */
    void normalize(ValidatorEngine engine) {
        while (getInherit().size() > 0) {
            List<FileType> files = Lists.newArrayList();
            List<String> inherits = Lists.newArrayList();

            for (String inherit : getInherit()) {
                ConfigurationType inherited = engine.getConfiguration(inherit);
                if (inherited != null) {
                    files.addAll(inherited.getFile());
                    inherits.addAll(inherited.getInherit());
                } else {
                    notLoaded.add(inherit);
                }
            }

            files.addAll(this.getFile());

            this.file = files;
            this.inherit = inherits;
        }
    }

    /**
     * Get list of resources not found when normalizing object.
     *
     * @return List of identifiers.
     */
    List<String> getNotLoaded() {
        return notLoaded;
    }

}
