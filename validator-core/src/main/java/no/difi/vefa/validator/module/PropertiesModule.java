package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.difi.vefa.validator.ValidatorDefaults;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.properties.CombinedProperties;

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
