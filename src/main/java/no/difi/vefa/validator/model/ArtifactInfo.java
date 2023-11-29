package no.difi.vefa.validator.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import no.difi.xsd.vefa.validator._1.ArtifactType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ArtifactInfo implements Comparable<ArtifactInfo> {

    private String path;

    private long timestamp;

    public static ArtifactInfo of(ArtifactType src) {
        return new ArtifactInfo(src.getFilename(), src.getTimestamp());
    }

    public static ArtifactInfo of(Path folder, Path path) {
        try {
            return new ArtifactInfo(path.toString().substring(folder.toString().length() + 1),
                    Files.getLastModifiedTime(path).to(TimeUnit.MILLISECONDS));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(ArtifactInfo o) {
        return path.compareTo(o.path);
    }
}
