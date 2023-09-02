package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.checker.SchematronCheckerFactory;
import no.difi.vefa.validator.checker.SchematronXsltCheckerFactory;
import no.difi.vefa.validator.checker.XsdCheckerFactory;

import java.util.List;
import java.util.Set;

public class CheckerModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<CheckerFactory> checkers = Multibinder.newSetBinder(binder(), CheckerFactory.class);
        checkers.addBinding().to(SchematronCheckerFactory.class);
        checkers.addBinding().to(SchematronXsltCheckerFactory.class);
        checkers.addBinding().to(XsdCheckerFactory.class);
    }

    @Provides
    @Singleton
    public List<CheckerFactory> getCheckerFactories(Set<CheckerFactory> factories) {
        return List.copyOf(factories);
    }
}
