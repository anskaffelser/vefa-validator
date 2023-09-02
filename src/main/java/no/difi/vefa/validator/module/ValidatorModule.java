package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import no.difi.vefa.validator.api.ConfigurationProvider;
import no.difi.vefa.validator.configuration.ValidatorTestConfigurationProvider;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author erlend
 */
public class ValidatorModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new CacheModule());
        install(new CheckerModule());
        install(new DetectorModule());
        install(new PropertiesModule());
        install(new SaxonModule());
        install(new SourceModule());
        install(new SchematronModule());

        Multibinder<ConfigurationProvider> configurations = Multibinder.newSetBinder(binder(), ConfigurationProvider.class);
        configurations.addBinding().to(ValidatorTestConfigurationProvider.class);
    }

    @Provides
    @Singleton
    public List<Configurations> getConfigurations(Set<ConfigurationProvider> providers) {
        return providers.stream()
                .map(ConfigurationProvider::getConfigurations)
                .collect(Collectors.toList());
    }
}
