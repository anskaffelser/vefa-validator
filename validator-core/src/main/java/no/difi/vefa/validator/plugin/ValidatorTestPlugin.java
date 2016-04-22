package no.difi.vefa.validator.plugin;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.api.ValidatorPlugin;
import no.difi.vefa.validator.declaration.ValidatorTestDeclaration;

import java.util.ArrayList;
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
        return new ArrayList<>();
    }

    @Override
    public List<Declaration> declarations() {
        return new ArrayList<Declaration>() {{
            add(new ValidatorTestDeclaration());
        }};
    }

    @Override
    public List<Class<? extends Renderer>> renderers() {
        return new ArrayList<>();
    }
}
