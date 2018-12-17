package no.difi.vefa.validator.build.module;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.difi.vefa.validator.api.Preparer;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author erlend
 */
public class BuildModule extends AbstractModule {

    @Provides
    @Singleton
    public List<Preparer> getPreparers(Injector injector) {
        List<Preparer> preparers = Lists.newArrayList(ServiceLoader.load(Preparer.class));

        for (Preparer preparer : preparers)
            injector.injectMembers(preparer);

        return Collections.unmodifiableList(preparers);
    }
}
