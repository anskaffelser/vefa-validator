package no.difi.vefa.validator.plugin;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.api.ValidatorPlugin;
import no.difi.vefa.validator.checker.AsiceTriggerChecker;
import no.difi.vefa.validator.declaration.AsiceDeclaration;

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
        return new ArrayList<Class<? extends Checker>>() {{
            add(AsiceTriggerChecker.class);
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
}
