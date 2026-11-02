package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.Feature;
import net.sf.saxon.lib.FeatureKeys;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.trans.XPathException;


/**
 * @author erlend
 */
public class SaxonModule extends AbstractModule {

    @Provides
    @Singleton
    public Processor getProcessor() {
        Configuration configuration = new Configuration();
        configuration.setConfigurationProperty(Feature.ALLOW_EXTERNAL_FUNCTIONS, false);
        configuration.setConfigurationProperty(Feature.XINCLUDE, false);
        configuration.setConfigurationProperty(Feature.ALLOWED_PROTOCOLS, "holder,classpath");
        configuration.setConfigurationProperty(FeatureKeys.XML_PARSER_FEATURE
                + "http://apache.org/xml/features/disallow-doctype-decl", true);
        configuration.setConfigurationProperty(FeatureKeys.XML_PARSER_FEATURE
                + "http://xml.org/sax/features/external-general-entities", false);
        configuration.setConfigurationProperty(FeatureKeys.XML_PARSER_FEATURE
                + "http://xml.org/sax/features/external-parameter-entities", false);
        configuration.setConfigurationProperty(FeatureKeys.XML_PARSER_FEATURE
                + "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        configuration.setResourceResolver(request -> {
            throw new XPathException("External XML resources are disabled");
        });
        configuration.setUnparsedTextURIResolver((uri, encoding, config, debug) -> {
            throw new XPathException("External text resources are disabled");
        });
        configuration.setCollectionFinder((context, uri) -> {
            throw new XPathException("External collections are disabled");
        });

        return new Processor(configuration);
    }
}
