package no.difi.vefa.validator;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerFactory;
import no.difi.vefa.validator.lang.ValidatorException;

import java.util.List;

/**
 * @author erlend
 */
@Slf4j
@Singleton
public class CheckerCacheLoader extends CacheLoader<String, Checker> {

    public static final int DEFAULT_SIZE = 250;

    @Inject
    private List<CheckerFactory> factories;

    @Inject
    private ValidatorEngine validatorEngine;

    @Override
    @NonNull
    public Checker load(@NonNull String key) throws Exception {
        try {
            for (CheckerFactory factory : factories) {
                for (String extension : factory.getClass().getAnnotation(Type.class).value()) {
                    if (key.toLowerCase().endsWith(extension)) {
                        return factory.prepare(validatorEngine.getResource(key), key.split("#")[1]);
                    }
                }
            }
        } catch (Exception e) {
            throw new ValidatorException(String.format("Unable to load checker for '%s'.", key), e);
        }

        throw new ValidatorException(String.format("No checker found for '%s'", key));
    }
}
