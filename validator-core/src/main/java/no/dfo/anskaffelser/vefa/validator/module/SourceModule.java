package no.dfo.anskaffelser.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.dfo.anskaffelser.vefa.validator.api.Properties;
import no.dfo.anskaffelser.vefa.validator.api.Source;
import no.dfo.anskaffelser.vefa.validator.api.SourceInstance;
import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;
import no.dfo.anskaffelser.vefa.validator.source.RepositorySource;

/**
 * @author erlend
 */
public class SourceModule extends AbstractModule {

    private final Source source;

    public SourceModule() {
        this(null);
    }

    public SourceModule(Source source) {
        this.source = source;
    }

    @Provides
    @Singleton
    public SourceInstance getSource(Properties properties) throws ValidatorException {
        // Make sure to default to repository source if no source is set.
        return (source != null ? source : RepositorySource.forProduction())
                .createInstance(properties);
    }
}
