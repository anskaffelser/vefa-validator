package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author erlend
 */
public class ConfigModule extends AbstractModule {

    @Provides
    @Singleton
    public Config getConfig() {
        Config config = ConfigFactory.load();
        config = config.withFallback(config.getConfig("defaults"));
        return config;
    }
}
