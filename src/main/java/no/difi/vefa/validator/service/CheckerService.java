package no.difi.vefa.validator.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.difi.vefa.validator.CheckerCacheLoader;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.model.Props;

import java.util.concurrent.TimeUnit;

@Singleton
public class CheckerService {

    private final LoadingCache<String, Checker> cache;

    @Inject
    public CheckerService(Props props, CheckerCacheLoader loader) {
        cache = CacheBuilder.newBuilder()
                .softValues()
                .maximumSize(props.getInt("pools.checker.size", 250))
                .expireAfterAccess(props.getLong("pools.checker.expire", TimeUnit.DAYS.toMinutes(1)), TimeUnit.MINUTES)
                .build(loader);
    }

    public Checker get(String key) throws Exception {
        return cache.get(key);
    }

    public void clear() {
        cache.invalidateAll();
        cache.cleanUp();
    }
}
