package no.difi.vefa.validator.util;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.nio.file.Path;

public class PathLSResolveResource implements LSResourceResolver {

    private Path rootPath;

    public PathLSResolveResource(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

        Path target;
        if (baseURI == null)
            target = rootPath.resolve(systemId);
        else
            target = rootPath.resolve(baseURI.replace("file:/", "/")).getParent().resolve(systemId);

        return new PathLSInput(target, publicId, systemId);
    }
}
