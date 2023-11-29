package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.checker.SchematronCheckerFactory;
import no.difi.vefa.validator.checker.SchematronXsltCheckerFactory;
import no.difi.vefa.validator.checker.XsdCheckerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    public Map<String, CheckerFactory> getMap(Set<CheckerFactory> factories) {
        Map<String, CheckerFactory> result = new HashMap<>();

        for (var factory : factories)
            for (var type : factory.types())
                result.put(type, factory);

        return Collections.unmodifiableMap(result);
    }
}
