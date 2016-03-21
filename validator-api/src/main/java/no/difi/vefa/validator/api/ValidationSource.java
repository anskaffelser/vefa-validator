package no.difi.vefa.validator.api;

import java.io.InputStream;

public interface ValidationSource {

    InputStream getInputStream();

    Properties getProperties();

}
