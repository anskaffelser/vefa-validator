package no.difi.vefa.validator.source;

import no.difi.vefa.validator.ValidatorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class DirectorySourceInstance extends AbstractSourceInstance {

    private static Logger logger = LoggerFactory.getLogger(DirectorySourceInstance.class);

    public DirectorySourceInstance(Path directory) throws ValidatorException {
        super();
        logger.info(String.format("Directory: %s", directory));

        for (File file : FileUtils.listFiles(directory.toFile(), new RegexFileFilter(".*\\.asice"), TrueFileFilter.INSTANCE)) {
            logger.info(String.format("Loading: %s", file));

            try {
                unpackContainer(asicReaderFactory.open(file), file.getName());
            } catch (IOException e) {
                logger.warn(e.getMessage());
                throw new ValidatorException(e.getMessage(), e);
            }
        }
    }
}
