package no.difi.vefa.validator.build.preparer;

import com.google.common.io.Files;
import no.difi.vefa.validator.api.Preparer;
import no.difi.vefa.validator.build.util.PreparerProvider;

import java.io.IOException;
import java.nio.file.Path;

public class DefaultPreparer implements Preparer {

    @Override
    public String[] types() {
        return new String[]{PreparerProvider.DEFAULT};
    }

    @Override
    public void prepare(Path source, Path target, Type type) throws IOException {
        Files.copy(source.toFile(), target.toFile());
    }
}
