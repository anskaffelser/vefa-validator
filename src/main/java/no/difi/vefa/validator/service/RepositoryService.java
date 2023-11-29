package no.difi.vefa.validator.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.xml.bind.JAXBContext;
import lombok.Getter;
import no.difi.vefa.validator.api.Repository;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.ArtifactInfo;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.util.JaxbUtils;
import no.difi.xsd.vefa.validator._1.Artifacts;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Service for loading and accessing repository.
 */
@Singleton
public class RepositoryService {

    private static final JAXBContext JAXB = JaxbUtils.context(Artifacts.class);

    @Inject
    @Nullable
    private Repository repository;

    @Getter
    private List<ArtifactInfo> listing;

    private long timestamp = -1;

    @Inject
    public void init() throws IOException, ValidatorException {
        // Trigger update
        update();
    }

    public InputStream fetch(ArtifactInfo artifact) throws IOException {
        if (Objects.isNull(repository))
            throw new IOException("File not found.");

        return repository.fetch(artifact.getPath());
    }

    public boolean update() throws IOException, ValidatorException {
        if (Objects.isNull(repository)) {
            listing = Collections.emptyList();
            return false;
        }

        var newListing = repository.fetchListing();
        var newTimestamp = newListing.stream()
                .map(ArtifactInfo::getTimestamp)
                .max(Long::compare)
                .orElse(0L);

        var newer = newTimestamp > this.timestamp;

        this.listing = newListing;
        this.timestamp = newTimestamp;

        return newer;
    }
}
