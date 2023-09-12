package no.difi.vefa.validator.source;

import jakarta.xml.bind.JAXBContext;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.model.ArtifactHolder;
import no.difi.vefa.validator.model.Props;
import no.difi.vefa.validator.util.JaxbUtils;
import no.difi.xsd.vefa.validator._1.Artifacts;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractSourceInstance implements SourceInstance, Closeable {

    protected static final JAXBContext JAXB_CONTEXT =
            JaxbUtils.context(Artifacts.class);

    protected Map<String, ArtifactHolder> content = new HashMap<>();

    protected void unpackContainer(InputStream inputStream, String targetName) throws IOException {
        content.put(targetName, ArtifactHolder.of(inputStream));
    }

    @Override
    public Map<String, ArtifactHolder> getContent() {
        return Collections.unmodifiableMap(content);
    }

    @Override
    public ArtifactHolder getContent(String path) {
        return content.get(path);
    }

    @Override
    public void close() {
        // No action.
    }
}
