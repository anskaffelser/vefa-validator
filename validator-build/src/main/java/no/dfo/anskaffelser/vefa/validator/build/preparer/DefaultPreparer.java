package no.dfo.anskaffelser.vefa.validator.build.preparer;

import com.google.common.io.Files;
import no.dfo.anskaffelser.vefa.validator.annotation.Type;
import no.dfo.anskaffelser.vefa.validator.api.Preparer;
import no.dfo.anskaffelser.vefa.validator.build.util.PreparerProvider;

import java.io.IOException;
import java.nio.file.Path;

@Type(PreparerProvider.DEFAULT)
public class DefaultPreparer implements Preparer {

    @Override
    public void prepare(Path source, Path target, Type type) throws IOException {
        Files.copy(source.toFile(), target.toFile());
    }
}
