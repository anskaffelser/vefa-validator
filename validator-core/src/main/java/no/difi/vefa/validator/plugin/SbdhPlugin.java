package no.difi.vefa.validator.plugin;

import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.builder.ConfigurationBuilder;
import no.difi.vefa.validator.builder.ConfigurationsBuilder;
import no.difi.vefa.validator.checker.SvrlXsltChecker;
import no.difi.vefa.validator.checker.XsdChecker;
import no.difi.vefa.validator.declaration.SbdhDeclaration;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SbdhPlugin implements ValidatorPlugin {

    @Override
    public List<String> capabilities() {
        return new ArrayList<String>() {{
            add("sbdh");
        }};
    }

    @Override
    public List<Class<? extends Checker>> checkers() {
        return new ArrayList<Class<? extends Checker>>() {{
            add(XsdChecker.class);
            add(SvrlXsltChecker.class);
        }};
    }

    @Override
    public List<Class<? extends Trigger>> triggers() {
        return Collections.emptyList();
    }

    @Override
    public List<Declaration> declarations() {
        return new ArrayList<Declaration>() {{
            add(new SbdhDeclaration());
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
                            .identifier("sbdh")
                            .title("Standard Business Document Header")
                            .standardId("SBDH:1.0")
                            .weight(Long.MIN_VALUE)
                            .build())
                    .build());
        }};
    }
}
