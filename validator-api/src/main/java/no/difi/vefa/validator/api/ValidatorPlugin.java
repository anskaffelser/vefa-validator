package no.difi.vefa.validator.api;

import java.util.List;

public interface ValidatorPlugin {
    List<String> capabilities();
    List<Class<? extends Checker>> checkers();
    List<Declaration> declarations();
    List<Class<? extends Renderer>> renderers();
}
