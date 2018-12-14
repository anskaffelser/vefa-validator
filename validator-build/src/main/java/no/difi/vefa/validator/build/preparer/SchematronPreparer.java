package no.difi.vefa.validator.build.preparer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.difi.commons.schematron.SchematronCompiler;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Build;
import no.difi.vefa.validator.api.Preparer;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;

@MetaInfServices
@Type({".sch", ".scmt"})
public class SchematronPreparer implements Preparer {

    @Inject
    private Provider<SchematronCompiler> schematronTransformer;

    @Override
    public void prepare(Path source, Path target) throws Exception {
        schematronTransformer.get().compile(source, target);
    }
}
