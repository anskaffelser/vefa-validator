package no.difi.vefa.validator.build.preparer;

import no.difi.commons.schematron.SchematronCompiler;
import no.difi.commons.schematron.SchematronException;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.api.build.Preparer;
import no.difi.vefa.validator.api.build.PreparerInfo;
import no.difi.vefa.validator.util.SaxonHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;

@PreparerInfo({".sch", ".scmt"})
@SuppressWarnings("unused")
public class SchematronPreparer implements Preparer {

    protected SchematronCompiler schematronTransformer;

    public SchematronPreparer() {
        try {
            this.schematronTransformer = new SchematronCompiler(SaxonHelper.PROCESSOR);
        } catch (SchematronException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ByteArrayOutputStream prepare(Build build, File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        schematronTransformer.compile(file, byteArrayOutputStream);
        return byteArrayOutputStream;
    }
}
