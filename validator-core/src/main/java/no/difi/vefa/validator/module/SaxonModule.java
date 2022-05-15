package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.Feature;
import net.sf.saxon.s9api.Processor;

import javax.xml.transform.TransformerException;

/**
 * @author erlend
 */
public class SaxonModule extends AbstractModule {

    @Provides
    @Singleton
    public Processor getProcessor() {
        Configuration configuration = new Configuration();
        configuration.setConfigurationProperty(Feature.ALLOW_EXTERNAL_FUNCTIONS, false);
        configuration.setURIResolver((href, base) -> {
            // Blocking accesses
            if (href.startsWith("http") || href.startsWith("ftp") || href.startsWith("file"))
                throw new TransformerException(String.format("Blocking request to '%s'.", href));

            return null;
        });

        return new Processor(configuration);
    }
}
