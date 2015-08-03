package no.difi.vefa.validator;

import no.difi.xsd.vefa.validator._1.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration extends ConfigurationType {

    private Map<String, RuleActionType> ruleActions = new HashMap<>();

    private List<String> notLoaded = new ArrayList<>();

    /**
     * Create new configuration based on configuration from XML.
     *
     * @param configurationType Configuration from XML.
     */
    Configuration(ConfigurationType configurationType) {
        // Copy rule
        this.setIdentifier(configurationType.getIdentifier());
        this.setTitle(configurationType.getTitle());
        this.setCustomizationId(configurationType.getCustomizationId());
        this.setProfileId(configurationType.getProfileId());
        this.setWeight(configurationType.getWeight());
        this.setBuild(configurationType.getBuild());
        this.inherit = configurationType.getInherit();
        this.rule = configurationType.getRule();
        this.file = configurationType.getFile();
    }

    void normalize(ValidatorEngine config) throws ValidatorException {
        while (getInherit().size() > 0) {
            List<RuleType> rules = new ArrayList<>();
            List<FileType> files = new ArrayList<>();
            List<String> inherits = new ArrayList<>();
            StylesheetType stylesheet = null;

            for (String inherit : getInherit()) {
                ConfigurationType inherited = config.getConfiguration(inherit);
                if (inherited != null) {
                    rules.addAll(inherited.getRule());
                    files.addAll(inherited.getFile());
                    inherits.addAll(inherited.getInherit());
                    if (inherited.getStylesheet() != null)
                        stylesheet = inherited.getStylesheet();
                } else {
                    notLoaded.add(inherit);
                }
            }

            rules.addAll(this.getRule());
            files.addAll(this.getFile());

            this.rule = rules;
            this.file = files;
            this.inherit = inherits;

            if (getStylesheet() == null)
                setStylesheet(stylesheet);
        }

        for (RuleType ruleType : this.getRule())
            ruleActions.put(ruleType.getIdentifier(), ruleType.getAction());
    }

    List<String> getNotLoaded() {
        return notLoaded;
    }

    public void filterFlag(AssertionType assertionType) {
        if (ruleActions.containsKey(assertionType.getIdentifier())) {
            switch (ruleActions.get(assertionType.getIdentifier())) {
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
