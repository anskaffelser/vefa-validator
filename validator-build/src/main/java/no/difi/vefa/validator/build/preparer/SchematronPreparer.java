package no.difi.vefa.validator.build.preparer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.difi.commons.schematron.SchematronCompiler;
import no.difi.vefa.validator.api.Type;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.api.build.Preparer;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayOutputStream;
import java.io.File;

@MetaInfServices
@Type({".sch", ".scmt"})
public class SchematronPreparer implements Preparer {

    @Inject
    private Provider<SchematronCompiler> schematronTransformer;

    public ByteArrayOutputStream prepare(Build build, File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        schematronTransformer.get().compile(file, byteArrayOutputStream);
        return byteArrayOutputStream;
    }
}
