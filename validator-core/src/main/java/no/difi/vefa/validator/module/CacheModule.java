package no.difi.vefa.validator.module;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.difi.vefa.validator.CheckerCacheLoader;
import no.difi.vefa.validator.RendererCacheLoader;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Renderer;

import java.util.concurrent.TimeUnit;

/**
 * @author erlend
 */
public class CacheModule extends AbstractModule {

    @Provides
    @Singleton
    public LoadingCache<String, Checker> getCheckerCache(Properties properties, CheckerCacheLoader loader) {
        return CacheBuilder.newBuilder()
                .maximumSize(properties.getInteger("pools.checker.size"))
                .expireAfterAccess(properties.getInteger("pools.checker.expire"), TimeUnit.MINUTES)
                .build(loader);
    }

    @Provides
    @Singleton
    public LoadingCache<String, Renderer> getRendererCache(Properties properties, RendererCacheLoader loader) {
        return CacheBuilder.newBuilder()
                .maximumSize(properties.getInteger("pools.presenter.size"))
                .expireAfterAccess(properties.getInteger("pools.presenter.expire"), TimeUnit.MINUTES)
                .build(loader);
    }
}
