package no.difi.vefa.validator.build.preparer;

import com.google.common.io.Files;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Build;
import no.difi.vefa.validator.api.Preparer;
import org.apache.commons.io.IOUtils;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

@MetaInfServices
@Type({".xsl", ".xslt"})
public class XsltPreparer implements Preparer {

    @Override
    public void prepare(Path source, Path target) throws Exception {
        Files.copy(source.toFile(), target.toFile());
    }
}
