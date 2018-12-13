package no.difi.vefa.validator.build.module;

import com.google.common.collect.Lists;
import com.google.inject.*;
import no.difi.vefa.validator.api.build.Preparer;
import org.kohsuke.MetaInfServices;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author erlend
 */
@MetaInfServices(Module.class)
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
