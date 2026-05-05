package no.dfo.anskaffelser.vefa.validator.source;

import no.dfo.anskaffelser.vefa.validator.api.Properties;
import no.dfo.anskaffelser.vefa.validator.api.SourceInstance;
import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

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
