package no.difi.vefa.validator.util;

import no.difi.vefa.validator.api.ArtifactHolder;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HolderURIResolver implements URIResolver {

    private ArtifactHolder artifactHolder;

    private Path rootPath;

    public HolderURIResolver(ArtifactHolder artifactHolder, String rootPath) {
        this.artifactHolder = artifactHolder;
        this.rootPath = Paths.get(rootPath).getParent();
    }

    @Override
    public Source resolve(String href, String base) {
        Path target = (base == "" ? rootPath : Paths.get(base.substring(7)).getParent()).resolve(href);

        String newPath = ("/" + target.toString().replaceAll("\\\\", "/")).replaceAll("/(.+?)/\\.\\.", "").substring(1);

        StreamSource streamSource = new StreamSource(artifactHolder.getInputStream(newPath));
        streamSource.setPublicId(String.format("holder:%s", newPath));
        streamSource.setSystemId(String.format("holder:%s", newPath));

        return streamSource;
    }
}
