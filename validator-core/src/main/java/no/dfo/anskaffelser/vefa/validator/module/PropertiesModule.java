package no.dfo.anskaffelser.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.dfo.anskaffelser.vefa.validator.ValidatorDefaults;
import no.dfo.anskaffelser.vefa.validator.api.Properties;
import no.dfo.anskaffelser.vefa.validator.properties.CombinedProperties;

/**
 * @author erlend
 */
public class PropertiesModule extends AbstractModule {

    private final Properties properties;

    public PropertiesModule() {
        this(null);
    }

    public PropertiesModule(Properties properties) {
        this.properties = properties;
    }

    @Provides
    @Singleton
    public Properties getProperties() {
        // Create config combined with default values.
        return new CombinedProperties(properties, ValidatorDefaults.PROPERTIES);
    }
}
