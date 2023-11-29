package no.difi.vefa.validator.util;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.service.ConfigurationService;

import java.util.Map;

/**
 * @author erlend
 */
@Slf4j
@Singleton
public class CheckerCacheLoader extends CacheLoader<String, Checker> {

    @Inject
    private Map<String, CheckerFactory> factories;

    @Inject
    private ConfigurationService validatorEngine;

    @Override
    @NonNull
    public Checker load(@NonNull String key) throws ValidatorException {
        try {
            for (var entry : factories.entrySet()) {
                if (key.toLowerCase().endsWith(entry.getKey())) {
                    return entry.getValue().prepare(validatorEngine.getResource(key), key.split("#")[1]);
                }
            }
        } catch (ValidatorException e) {
            throw new ValidatorException(String.format("Unable to load checker for '%s'.", key), e);
        }

        throw new ValidatorException(String.format("No checker found for '%s'", key));
    }
}
