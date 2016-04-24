package no.difi.vefa.validator.plugin;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.builder.ConfigurationBuilder;
import no.difi.vefa.validator.builder.ConfigurationsBuilder;
import no.difi.vefa.validator.declaration.ValidatorTestDeclaration;
import no.difi.vefa.validator.declaration.ValidatorTestSetDeclaration;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidatorTestPlugin implements ValidatorPlugin {

    @Override
    public List<String> capabilities() {
        return new ArrayList<String>() {{
            add("test");
        }};
    }

    @Override
    public List<Class<? extends Checker>> checkers() {
        return Collections.emptyList();
    }

    @Override
    public List<Class<? extends Trigger>> triggers() {
        return Collections.emptyList();
    }

    @Override
    public List<Declaration> declarations() {
        return new ArrayList<Declaration>() {{
            add(new ValidatorTestDeclaration());
            add(new ValidatorTestSetDeclaration());
        }};
    }

    @Override
    public List<Class<? extends Renderer>> renderers() {
        return Collections.emptyList();
    }

    @Override
    public List<Configurations> configurations() {
        return new ArrayList<Configurations>() {{
            add(ConfigurationsBuilder
                    .instance()
                    .configuration(ConfigurationBuilder
                            .identifier("vefa-testset")
                            .title("VEFA Validator Test Set")
                            .standardId("http://difi.no/xsd/vefa/validator/1.0::testSet")
                            .weight(Long.MIN_VALUE)
                            .build())
                    .build());
        }};
    }
}
