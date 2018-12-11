package no.difi.vefa.validator.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class PathURIResolver implements URIResolver {

    private Path rootPath;

    public PathURIResolver(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        Path target = rootPath.resolve(href);

        log.debug(String.valueOf(target));
        try {
            return new StreamSource(Files.newInputStream(target));
        } catch (IOException e) {
            throw new TransformerException(e.getMessage(), e);
        }
    }
}
