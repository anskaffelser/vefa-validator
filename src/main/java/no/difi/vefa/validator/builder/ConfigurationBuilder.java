package no.difi.vefa.validator.builder;

import no.difi.xsd.vefa.validator._1.ConfigurationType;
import no.difi.xsd.vefa.validator._1.DeclarationType;
import no.difi.xsd.vefa.validator._1.IdentifierType;

@Deprecated
public class ConfigurationBuilder {

    private final ConfigurationType configuration = new ConfigurationType();

    public static ConfigurationBuilder identifier(String identifier) {
        return new ConfigurationBuilder(identifier);
    }

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

    public ConfigurationBuilder declaration(String type, String value) {
        DeclarationType declarationType = new DeclarationType();
        declarationType.setType(type);
        declarationType.setValue(value);
        configuration.getDeclaration().add(declarationType);
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