package no.difi.vefa.validator.build.preparer;

import no.difi.vefa.validator.build.api.Build;
import no.difi.vefa.validator.build.api.Preparer;
import no.difi.vefa.validator.build.api.PreparerInfo;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

@PreparerInfo(".xsd")
public class XsdPreparer implements Preparer {

    @Override
    public ByteArrayOutputStream prepare(Build build, File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream inputStream = new FileInputStream(file);

        IOUtils.copy(inputStream, byteArrayOutputStream);

        inputStream.close();

        return byteArrayOutputStream;
    }
}
