package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.model.Props;
import no.difi.vefa.validator.source.RepositorySource;

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
    public SourceInstance getSource(Props props) throws ValidatorException {
        // Make sure to default to repository source if no source is set.
        return (source != null ? source : RepositorySource.forProduction()).createInstance();
    }
}
