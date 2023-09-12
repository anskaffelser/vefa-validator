package no.difi.vefa.validator.util;

import no.difi.vefa.validator.model.ArtifactHolder;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.nio.file.Paths;

public class HolderLSResolveResource implements LSResourceResolver {

    private final ArtifactHolder artifactHolder;


    public HolderLSResolveResource(ArtifactHolder artifactHolder) {
        this.artifactHolder = artifactHolder;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        var target = Paths.get(baseURI.substring(7)).getParent().resolve(systemId);

        String newPath = ("/" + target.toString().replaceAll("\\\\", "/")).replaceAll("/([^/]+?)/\\.\\.", "").substring(1);

        return new HolderLSInput(artifactHolder.get(newPath), newPath);
    }
}
