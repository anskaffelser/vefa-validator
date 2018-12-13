package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.difi.vefa.validator.ValidatorDefaults;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.vefa.validator.source.RepositorySource;

/**
 * @author erlend
 */
public class ConfigurationModule extends AbstractModule {

    private Source source;

    private Properties properties;

    public ConfigurationModule(Source source, Properties properties) {
        this.source = source;
        this.properties = properties;
    }

    @Provides
    @Singleton
    public SourceInstance getSource(Properties properties) throws ValidatorException {
        // Make sure to default to repository source if no source is set.
        return (source != null ? source : RepositorySource.forProduction())
                .createInstance(properties);
    }

    @Provides
    @Singleton
    public Properties getProperties() {
        // Create config combined with default values.
        return new CombinedProperties(properties, ValidatorDefaults.PROPERTIES);
    }
}
