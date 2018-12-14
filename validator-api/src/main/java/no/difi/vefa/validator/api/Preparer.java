package no.difi.vefa.validator.api;

import java.nio.file.Path;

public interface Preparer {

    void prepare(Path source, Path target) throws Exception;

}
