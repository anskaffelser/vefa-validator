package no.difi.vefa.validator;

import com.google.common.collect.Lists;
import no.difi.vefa.validator.api.FlagFilterer;
import no.difi.xsd.vefa.validator._1.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configurations found in validation artifacts are updated to this kind of object.
 */
class Configuration extends ConfigurationType implements FlagFilterer {

    private final Map<String, RuleActionType> ruleActions = new HashMap<>();

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
        this.rule = configurationType.getRule();
        this.file = configurationType.getFile();
        this.trigger = configurationType.getTrigger();
    }

    /**
     * Validation artifacts supports inheritance. This methods uses inheritance references to generate
     * a complete object containing all resources for a given document type.
     *
     * @param engine ValidatiorEngine for fetching of other configurations.
     */
    void normalize(ValidatorEngine engine) {
        while (getInherit().size() > 0) {
            List<RuleType> rules = Lists.newArrayList();
            List<FileType> files = Lists.newArrayList();
            List<TriggerType> triggers = Lists.newArrayList();
            List<String> inherits = Lists.newArrayList();

            for (String inherit : getInherit()) {
                ConfigurationType inherited = engine.getConfiguration(inherit);
                if (inherited != null) {
                    rules.addAll(inherited.getRule());
                    files.addAll(inherited.getFile());
                    triggers.addAll(inherited.getTrigger());
                    inherits.addAll(inherited.getInherit());
                } else {
                    notLoaded.add(inherit);
                }
            }

            rules.addAll(this.getRule());
            files.addAll(this.getFile());
            triggers.addAll(this.getTrigger());

            this.rule = rules;
            this.file = files;
            this.trigger = triggers;
            this.inherit = inherits;
        }

        for (RuleType ruleType : this.getRule())
            ruleActions.put(ruleType.getIdentifier(), ruleType.getAction());
    }

    /**
     * Get list of resources not found when normalizing object.
     *
     * @return List of identifiers.
     */
    List<String> getNotLoaded() {
        return notLoaded;
    }

    public void filterFlag(AssertionType assertionType) {
        if (ruleActions.containsKey(assertionType.getIdentifier())) {
            switch (ruleActions.get(assertionType.getIdentifier())) {
                case SET_FUTURE_ERROR:
                    assertionType.setFlag(FlagType.FUTURE_ERROR);
                    break;
                case SET_ERROR:
                    assertionType.setFlag(FlagType.ERROR);
                    break;
                case SET_WARNING:
                    assertionType.setFlag(FlagType.WARNING);
                    break;
                case SET_FATAL:
                    assertionType.setFlag(FlagType.FATAL);
                    break;
                case SUPPRESS:
                    assertionType.setFlag(null);
                    break;
            }
        }
    }
}
