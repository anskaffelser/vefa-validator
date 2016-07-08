package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.api.ValidatorException;

import java.util.Set;

public class ClasspathSource extends AbstractSource {

    private String location;

    public ClasspathSource(String location) {
        this.location = location;
    }

    @Override
    public SourceInstance createInstance(Properties properties) throws ValidatorException {
        return new ClasspathSourceInstance(properties, location);
    }
}
