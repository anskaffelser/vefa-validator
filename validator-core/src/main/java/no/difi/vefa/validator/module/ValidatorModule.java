package no.difi.vefa.validator.module;

import com.google.common.collect.Lists;
import com.google.inject.*;
import no.difi.vefa.validator.api.*;
import no.difi.xsd.vefa.validator._1.Configurations;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author erlend
 */
@MetaInfServices(Module.class)
public class ValidatorModule extends AbstractModule {

    @Provides
    @Singleton
    public List<CheckerFactory> getCheckerFactories(Injector injector) {
        List<CheckerFactory> factories = Lists.newArrayList(ServiceLoader.load(CheckerFactory.class));

        for (CheckerFactory factory : factories)
            injector.injectMembers(factory);

        return Collections.unmodifiableList(factories);
    }

    @Provides
    @Singleton
    public List<RendererFactory> getRendererFactories(Injector injector) {
        List<RendererFactory> factories = Lists.newArrayList(ServiceLoader.load(RendererFactory.class));

        for (RendererFactory factory : factories)
            injector.injectMembers(factory);

        return Collections.unmodifiableList(factories);
    }

    @Provides
    @Singleton
    public List<Trigger> getTriggers(Injector injector) {
        List<Trigger> triggers = Lists.newArrayList(ServiceLoader.load(Trigger.class));

        for (Trigger trigger : triggers)
            injector.injectMembers(trigger);

        return Collections.unmodifiableList(triggers);
    }

    @Provides
    @Singleton
    public List<Declaration> getDeclarations(Injector injector) {
        List<Declaration> declarations = Lists.newArrayList(ServiceLoader.load(Declaration.class));

        for (Declaration declaration : declarations)
            injector.injectMembers(declaration);

        return Collections.unmodifiableList(declarations);
    }

    @Provides
    @Singleton
    public List<Configurations> getConfigurations() {
        List<Configurations> configurations = new ArrayList<>();

        for (ConfigurationProvider provider : ServiceLoader.load(ConfigurationProvider.class))
            configurations.add(provider.getConfigurations());

        return configurations;
    }
}
