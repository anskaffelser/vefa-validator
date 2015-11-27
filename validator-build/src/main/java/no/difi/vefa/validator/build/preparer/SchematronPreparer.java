package no.difi.vefa.validator.build.preparer;

import no.difi.commons.schematron.SchematronTransformer;
import no.difi.vefa.validator.build.api.Build;
import no.difi.vefa.validator.build.api.Preparer;
import no.difi.vefa.validator.build.api.PreparerInfo;

import javax.xml.transform.TransformerConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;

@PreparerInfo({".sch", ".scmt"})
public class SchematronPreparer implements Preparer {

    protected SchematronTransformer schematronTransformer;

    public SchematronPreparer() {
        try {
            this.schematronTransformer = new SchematronTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ByteArrayOutputStream prepare(Build build, File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        schematronTransformer.transform(file, byteArrayOutputStream);
        return byteArrayOutputStream;
    }
}
