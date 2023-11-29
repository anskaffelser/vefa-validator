package no.difi.vefa.validator.util;

import no.difi.vefa.validator.api.Repository;
import no.difi.vefa.validator.model.ArtifactInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public interface Repositories {

    static Repository test() {
        return url("https://anskaffelser.dev/repo/validator/draft/");
    }

    static Repository production() {
        return url("https://anskaffelser.dev/repo/validator/current/");
    }

    static Repository classpath(String folder) {
        var f = Path.of(folder);

        return path -> {
            var stream = Repositories.class.getResourceAsStream(f.resolve(path).toString());

            if (Objects.isNull(stream))
                throw new FileNotFoundException();

            return stream;
        };
    }

    static Repository folder(Path folder) {
        return new Repository() {
            @Override
            public InputStream fetch(String path) throws IOException {
                var p = folder.resolve(path);

                if (Files.notExists(p))
                    throw new FileNotFoundException();

                return Files.newInputStream(p);
            }

            @Override
            public List<ArtifactInfo> listingFallback() throws IOException {
                try (var stream = Files.walk(folder, FileVisitOption.FOLLOW_LINKS)) {
                    return stream
                            .filter(p -> !p.equals(folder))
                            .filter(Files::isRegularFile)
                            .map(p -> ArtifactInfo.of(folder, p))
                            .sorted()
                            .toList();
                }
            }
        };
    }

    static Repository folder(String folder) {
        return folder(Paths.get(folder));
    }

    static Repository url(String url) {
        return url(URI.create(url));
    }

    static Repository url(URI uri) {
        return path -> uri.resolve(path).toURL().openStream();
    }
}
