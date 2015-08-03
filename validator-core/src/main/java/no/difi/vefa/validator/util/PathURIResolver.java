package no.difi.vefa.validator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathURIResolver implements URIResolver {

    private static Logger logger = LoggerFactory.getLogger(PathURIResolver.class);

    private Path rootPath;

    public PathURIResolver(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        Path target = rootPath.resolve(href);

        logger.debug(String.valueOf(target));
        try {
            return new StreamSource(Files.newInputStream(target));
        } catch (IOException e) {
            throw new TransformerException(e.getMessage(), e);
        }
    }
}
