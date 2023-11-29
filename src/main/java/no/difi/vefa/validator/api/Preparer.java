package no.difi.vefa.validator.api;

import java.io.IOException;
import java.nio.file.Path;

public interface Preparer {

    String[] types();

    void prepare(Path source, Path target, Type type) throws IOException;

    enum Type {
        FILE,
        INCLUDE
    }
}
