package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;
import no.difi.vefa.validator.api.Repository;
import no.difi.vefa.validator.model.Prop;
import no.difi.vefa.validator.model.Props;
import no.difi.vefa.validator.service.ConfigurationService;

import java.util.Objects;

/**
 * @author erlend
 */
public class ValidatorModule extends AbstractModule {

    private final Repository repository;

    private final Props props;

    public ValidatorModule(Repository repository, Prop... props) {
        this.repository = repository;
        this.props = Props.init().update(props);
    }

    @Override
    protected void configure() {
        install(new CheckerModule());
        install(new DetectorModule());
        install(new SaxonModule());
        install(new SchematronModule());
        install(new TestingModule());

        bind(Props.class).toInstance(props);

        if (Objects.isNull(repository))
            bind(Repository.class).toProvider(Providers.of(null));
        else
            bind(Repository.class).toInstance(repository);

        bind(ConfigurationService.class).asEagerSingleton();
    }
}
