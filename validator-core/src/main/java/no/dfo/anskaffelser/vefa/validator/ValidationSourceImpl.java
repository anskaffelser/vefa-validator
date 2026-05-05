package no.dfo.anskaffelser.vefa.validator;

import no.dfo.anskaffelser.vefa.validator.api.Properties;
import no.dfo.anskaffelser.vefa.validator.api.ValidationSource;

import java.io.InputStream;

class ValidationSourceImpl implements ValidationSource {

    private InputStream inputStream;

    private Properties properties;

    public ValidationSourceImpl(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ValidationSourceImpl(InputStream inputStream, Properties properties) {
        this(inputStream);
        this.properties = properties;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
