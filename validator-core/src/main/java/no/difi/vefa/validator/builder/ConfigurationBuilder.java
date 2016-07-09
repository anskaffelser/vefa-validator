package no.difi.vefa.validator.builder;

import no.difi.xsd.vefa.validator._1.ConfigurationType;
import no.difi.xsd.vefa.validator._1.IdentifierType;
import no.difi.xsd.vefa.validator._1.TriggerType;

public class ConfigurationBuilder {

    public static ConfigurationBuilder identifier(String identifier) {
        return new ConfigurationBuilder(identifier);
    }

    private ConfigurationType configuration = new ConfigurationType();

    private ConfigurationBuilder(String identifier) {
        IdentifierType identifierType = new IdentifierType();
        identifierType.setValue(identifier);


        configuration.setIdentifier(identifierType);
        configuration.setBuild("code");
    }

    public ConfigurationBuilder title(String title) {
        configuration.setTitle(title);
        return this;
    }

    public ConfigurationBuilder build(String build) {
        configuration.setBuild(build);
        return this;
    }

    public ConfigurationBuilder standardId(String standardId) {
        configuration.setStandardId(standardId);
        return this;
    }

    public ConfigurationBuilder trigger(String identifier) {
        TriggerType trigger = new TriggerType();
        trigger.setIdentifier(identifier);

        configuration.getTrigger().add(trigger);
        return this;
    }

    public ConfigurationBuilder weight(long weight) {
        configuration.setWeight(weight);
        return this;
    }

    public ConfigurationType build() {
        return configuration;
    }
}