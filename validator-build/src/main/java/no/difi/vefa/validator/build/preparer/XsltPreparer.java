package no.difi.vefa.validator.build.preparer;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Build;
import no.difi.vefa.validator.api.Preparer;
import org.apache.commons.io.IOUtils;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

@MetaInfServices
@Type({".xsl", ".xslt"})
public class XsltPreparer implements Preparer {

    @Override
    public ByteArrayOutputStream prepare(Build build, File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream inputStream = new FileInputStream(file);

        IOUtils.copy(inputStream, byteArrayOutputStream);

        inputStream.close();

        return byteArrayOutputStream;
    }
}
