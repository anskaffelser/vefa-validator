package no.difi.vefa.validator.build.preparer;

import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.api.build.Preparer;
import no.difi.vefa.validator.api.build.PreparerInfo;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Simple class copying file, used for generic files to trigger non-file based checkers.
 */
@PreparerInfo(".trigger")
public class TriggerPreparer implements Preparer {
    @Override
    public ByteArrayOutputStream prepare(Build build, File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream inputStream = new FileInputStream(file);

        IOUtils.copy(inputStream, byteArrayOutputStream);

        inputStream.close();

        return byteArrayOutputStream;

    }
}
