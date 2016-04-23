package no.difi.vefa.validator.builder;

import no.difi.xsd.vefa.validator._1.ConfigurationType;
import no.difi.xsd.vefa.validator._1.Configurations;
import no.difi.xsd.vefa.validator._1.PackageType;

public class ConfigurationsBuilder {

    public static ConfigurationsBuilder instance() {
        return new ConfigurationsBuilder();
    }

    private Configurations configurations = new Configurations();

    private ConfigurationsBuilder() {
        configurations.setTimestamp(0L);
    }

    public ConfigurationsBuilder name(String name) {
        configurations.setName(name);
        return this;
    }

    public ConfigurationsBuilder pkg(String title) {
        return pkg(title, null);
    }

    public ConfigurationsBuilder pkg(String title, String url) {
        PackageType packageType = new PackageType();
        packageType.setValue(title);
        packageType.setUrl(url);

        configurations.getPackage().add(packageType);
        return this;
    }

    public ConfigurationsBuilder configuration(ConfigurationType configuration) {
        configurations.getConfiguration().add(configuration);
        return this;
    }

    public Configurations build() {
        return configurations;
    }
}
