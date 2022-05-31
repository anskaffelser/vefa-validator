package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.Feature;
import net.sf.saxon.s9api.Processor;
import no.difi.vefa.validator.util.BlockingURIResolver;
import org.kohsuke.MetaInfServices;

/**
 * @author erlend
 */
@MetaInfServices(Module.class)
public class SaxonModule extends AbstractModule {

    @Provides
    @Singleton
    public Processor getProcessor() {
        Configuration configuration = new Configuration();
        configuration.setConfigurationProperty(Feature.ALLOW_EXTERNAL_FUNCTIONS, false);
        configuration.setURIResolver(new BlockingURIResolver());

        return new Processor(configuration);
    }
}
