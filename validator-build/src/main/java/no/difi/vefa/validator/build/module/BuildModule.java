package no.difi.vefa.validator.build.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import no.difi.vefa.validator.api.Preparer;
import no.difi.vefa.validator.build.preparer.DefaultPreparer;
import no.difi.vefa.validator.build.preparer.SchematronPreparer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author erlend
 */
public class BuildModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Preparer> preparers = Multibinder.newSetBinder(binder(), Preparer.class);
        preparers.addBinding().to(DefaultPreparer.class);
        preparers.addBinding().to(SchematronPreparer.class);
    }

    @Provides
    @Singleton
    public List<Preparer> getPreparers(Set<Preparer> preparers) {
        return Collections.unmodifiableList(new ArrayList<>(preparers));
    }
}
