package no.difi.vefa.validator.build.api;

import java.io.ByteArrayOutputStream;
import java.io.File;

public interface Preparer {
    ByteArrayOutputStream prepare(File file) throws Exception;
}
