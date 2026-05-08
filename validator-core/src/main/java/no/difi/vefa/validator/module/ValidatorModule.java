package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.checker.SchematronCheckerFactory;
import no.difi.vefa.validator.checker.SchematronXsltCheckerFactory;
import no.difi.vefa.validator.checker.XsdCheckerFactory;
import no.difi.vefa.validator.configuration.AsiceConfigurationProvider;
import no.difi.vefa.validator.configuration.ValidatorTestConfigurationProvider;
import no.difi.vefa.validator.declaration.*;
import no.difi.vefa.validator.renderer.XsltRendererFactory;
import no.difi.vefa.validator.trigger.AsiceTrigger;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.util.ArrayList;
import java.util.Collections;
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
        install(new PropertiesModule());
        install(new SaxonModule());
        install(new SbdhModule());
        install(new SourceModule());
        install(new SchematronModule());

        Multibinder<CheckerFactory> checkers = Multibinder.newSetBinder(binder(), CheckerFactory.class);
        checkers.addBinding().to(SchematronCheckerFactory.class);
        checkers.addBinding().to(SchematronXsltCheckerFactory.class);
        checkers.addBinding().to(XsdCheckerFactory.class);

        Multibinder<RendererFactory> renderers = Multibinder.newSetBinder(binder(), RendererFactory.class);
        renderers.addBinding().to(XsltRendererFactory.class);

        Multibinder<Trigger> triggers = Multibinder.newSetBinder(binder(), Trigger.class);
        triggers.addBinding().to(AsiceTrigger.class);

        Multibinder<Declaration> declarations = Multibinder.newSetBinder(binder(), Declaration.class);
        declarations.addBinding().to(AsiceDeclaration.class);
        declarations.addBinding().to(AsiceXmlDeclaration.class);
        declarations.addBinding().to(EspdDeclaration.class);
        declarations.addBinding().to(NoblDeclaration.class);
        declarations.addBinding().to(SbdhDeclaration.class);
        declarations.addBinding().to(UblDeclaration.class);
        declarations.addBinding().to(UnCefactDeclaration.class);
        declarations.addBinding().to(ValidatorTestDeclaration.class);
        declarations.addBinding().to(ValidatorTestSetDeclaration.class);
        declarations.addBinding().to(XmlDeclaration.class);
        declarations.addBinding().to(ZipDeclaration.class);

        Multibinder<ConfigurationProvider> configurations = Multibinder.newSetBinder(binder(), ConfigurationProvider.class);
        configurations.addBinding().to(AsiceConfigurationProvider.class);
        configurations.addBinding().to(ValidatorTestConfigurationProvider.class);
    }

    @Provides
    @Singleton
    public List<CheckerFactory> getCheckerFactories(Set<CheckerFactory> factories) {
        return Collections.unmodifiableList(new ArrayList<>(factories));
    }

    @Provides
    @Singleton
    public List<RendererFactory> getRendererFactories(Set<RendererFactory> factories) {
        return Collections.unmodifiableList(new ArrayList<>(factories));
    }

    @Provides
    @Singleton
    public List<Trigger> getTriggers(Set<Trigger> triggers) {
        return Collections.unmodifiableList(new ArrayList<>(triggers));
    }

    @Provides
    @Singleton
    public List<Declaration> getDeclarations(Set<Declaration> declarations) {
        return Collections.unmodifiableList(new ArrayList<>(declarations));
    }

    @Provides
    @Singleton
    public List<Configurations> getConfigurations(Set<ConfigurationProvider> providers) {
        return providers.stream()
                .map(ConfigurationProvider::getConfigurations)
                .collect(Collectors.toList());
    }
}
