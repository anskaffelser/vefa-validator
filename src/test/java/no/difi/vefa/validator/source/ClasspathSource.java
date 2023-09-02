package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.lang.ValidatorException;

public class ClasspathSource implements Source {

    private final String location;

    public ClasspathSource(String location) {
        this.location = location;
    }

    @Override
    public SourceInstance createInstance(Properties properties) throws ValidatorException {
        return new ClasspathSourceInstance(properties, location);
    }
}
