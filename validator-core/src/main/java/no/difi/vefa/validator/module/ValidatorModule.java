package no.difi.vefa.validator.module;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.difi.vefa.validator.ValidatorDefaults;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.properties.CombinedProperties;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author erlend
 */
public class ValidatorModule extends AbstractModule {

    private Source source;

    private Properties properties;

    public ValidatorModule(Source source, Properties properties) {
        this.source = source;
        this.properties = properties;
    }

    @Provides
    @Singleton
    public List<CheckerFactory> getCheckerFactories() {
        return Collections.unmodifiableList(Lists.newArrayList(ServiceLoader.load(CheckerFactory.class)));
    }

    @Provides
    @Singleton
    public List<RendererFactory> getRendererFactories() {
        return Collections.unmodifiableList(Lists.newArrayList(ServiceLoader.load(RendererFactory.class)));
    }

    @Provides
    @Singleton
    public List<Trigger> getTriggers() {
        return Collections.unmodifiableList(Lists.newArrayList(ServiceLoader.load(Trigger.class)));
    }

    @Provides
    @Singleton
    public List<Configurations> getConfigurations() {
        List<Configurations> configurations = new ArrayList<>();

        for (ConfigurationProvider provider : ServiceLoader.load(ConfigurationProvider.class))
            configurations.add(provider.getConfigurations());

        return configurations;
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
