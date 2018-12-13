package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sf.saxon.s9api.Processor;
import org.kohsuke.MetaInfServices;

/**
 * @author erlend
 */
@MetaInfServices(Module.class)
public class SaxonModule extends AbstractModule {

    @Provides
    @Singleton
    public Processor getProcessor() {
        return new Processor(false);
    }
}
