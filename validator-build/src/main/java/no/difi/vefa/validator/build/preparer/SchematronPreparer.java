package no.difi.vefa.validator.build.preparer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import no.difi.commons.schematron.SchematronCompiler;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Preparer;
import org.kohsuke.MetaInfServices;

import java.nio.file.Path;

@MetaInfServices
@Type({".sch", ".scmt"})
public class SchematronPreparer implements Preparer {

    @Inject
    @Named("compile")
    private Provider<SchematronCompiler> schematronCompile;

    @Inject
    @Named("prepare")
    private Provider<SchematronCompiler> schematronPrepare;

    @Override
    public void prepare(Path source, Path target) throws Exception {
        if (target.toString().endsWith(".sch")) {
            schematronPrepare.get().compile(source, target);
        } else {
            schematronCompile.get().compile(source, target);
        }
    }
}
