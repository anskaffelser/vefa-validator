package no.difi.vefa.validator.build.preparer;

import com.google.common.io.Files;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Preparer;
import org.kohsuke.MetaInfServices;

import java.nio.file.Path;

@MetaInfServices
@Type(".xsd")
public class XsdPreparer implements Preparer {

    @Override
    public void prepare(Path source, Path target) throws Exception {
        Files.copy(source.toFile(), target.toFile());
    }
}
