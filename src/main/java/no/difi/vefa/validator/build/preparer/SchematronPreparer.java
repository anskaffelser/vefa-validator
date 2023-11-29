package no.difi.vefa.validator.build.preparer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import no.difi.commons.schematron.SchematronCompiler;
import no.difi.commons.schematron.SchematronException;
import no.difi.vefa.validator.api.Preparer;

import java.io.IOException;
import java.nio.file.Path;

public class SchematronPreparer implements Preparer {

    @Inject
    @Named("compile")
    private Provider<SchematronCompiler> schematronCompile;

    @Inject
    @Named("prepare")
    private Provider<SchematronCompiler> schematronPrepare;

    @Override
    public String[] types() {
        return new String[]{".sch", ".scmt"};
    }

    @Override
    public void prepare(Path source, Path target, Type type) throws IOException {
        try {
            if (target.toString().endsWith(".sch")) {
                schematronPrepare.get().compile(source, target);
            } else {
                schematronCompile.get().compile(source, target);
            }
        } catch (SchematronException e) {
            throw new IOException("Unable to handle Schematron.", e);
        }
    }
}
