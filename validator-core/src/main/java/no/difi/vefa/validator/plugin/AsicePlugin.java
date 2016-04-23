package no.difi.vefa.validator.plugin;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.builder.ConfigurationBuilder;
import no.difi.vefa.validator.builder.ConfigurationsBuilder;
import no.difi.vefa.validator.declaration.AsiceDeclaration;
import no.difi.vefa.validator.trigger.AsiceTrigger;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.ArrayList;
import java.util.List;

public class AsicePlugin implements ValidatorPlugin {
    @Override
    public List<String> capabilities() {
        return new ArrayList<String>() {{
            add("asice");
        }};
    }

    @Override
    public List<Class<? extends Checker>> checkers() {
        return new ArrayList<>();
    }

    @Override
    public List<Class<? extends Trigger>> triggers() {
        return new ArrayList<Class<? extends Trigger>>() {{
            add(AsiceTrigger.class);
        }};
    }

    @Override
    public List<Declaration> declarations() {
        return new ArrayList<Declaration>() {{
            add(new AsiceDeclaration());
        }};
    }

    @Override
    public List<Class<? extends Renderer>> renderers() {
        return new ArrayList<>();
    }

    @Override
    public List<Configurations> configurations() {
        return new ArrayList<Configurations>() {{
            add(ConfigurationsBuilder
                            .instance()
                            // .pkg("ASiC-E")
                            .configuration(ConfigurationBuilder
                                    .identifier("asice-archive")
                                    .title("ASiC-E")
                                    .standardId("ASiC-E")
                                    .trigger("asice")
                                    .weight(Long.MIN_VALUE)
                                    .build())
                            .build()
            );
        }};
    }
}
