package no.difi.vefa.validator.source;

import lombok.extern.slf4j.Slf4j;
import no.difi.asic.AsicReader;
import no.difi.asic.AsicReaderFactory;
import no.difi.asic.SignatureMethod;
import no.difi.vefa.validator.api.ArtifactHolder;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.util.ArtifactHolderImpl;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.Artifacts;

import javax.xml.bind.JAXBContext;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractSourceInstance implements SourceInstance, Closeable {

    protected static final AsicReaderFactory ASIC_READER_FACTORY =
            AsicReaderFactory.newFactory(SignatureMethod.CAdES);

    protected static final JAXBContext JAXB_CONTEXT =
            JAXBHelper.context(Artifacts.class);

    protected Properties properties;

    protected Map<String, ArtifactHolder> content = new HashMap<>();

    public AbstractSourceInstance(Properties properties) {
        this.properties = properties;
    }

    protected void unpackContainer(AsicReader asicReader, String targetName) throws IOException {
        content.put(targetName, ArtifactHolderImpl.load(asicReader));
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
