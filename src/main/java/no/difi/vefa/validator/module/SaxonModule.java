package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.Feature;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;
import no.difi.vefa.validator.util.ContentLoader;


/**
 * @author erlend
 */
public class SaxonModule extends AbstractModule {

    @Provides
    @Singleton
    public Processor getProcessor() {
        Configuration configuration = new Configuration();
        configuration.setConfigurationProperty(Feature.ALLOW_EXTERNAL_FUNCTIONS, false);

        return new Processor(configuration);
    }

    @Provides
    public XsltCompiler getCompiler(Processor processor, ContentLoader resourceResolver) {
        var compiler = processor.newXsltCompiler();
        compiler.setResourceResolver(resourceResolver);

        return compiler;
    }
}
