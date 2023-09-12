package no.difi.vefa.validator.configuration;

import no.difi.vefa.validator.api.ConfigurationProvider;
import no.difi.vefa.validator.builder.ConfigurationBuilder;
import no.difi.vefa.validator.builder.ConfigurationsBuilder;
import no.difi.xsd.vefa.validator._1.Configurations;

@Deprecated
public class ValidatorTestConfigurationProvider implements ConfigurationProvider {

    @Override
    public Configurations getConfigurations() {
        return ConfigurationsBuilder
                .instance()
                .configuration(ConfigurationBuilder
                        .identifier("vefa-testset")
                        .title("VEFA Validator Test Set")
                        .declaration("xml.testset", "http://difi.no/xsd/vefa/validator/1.0::testSet")
                        .weight(Long.MIN_VALUE)
                        .build())
                .build();
    }
}
