package no.difi.vefa.validator;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerInfo;
import no.difi.vefa.validator.api.ValidatorException;

import java.util.Set;

/**
 * @author erlend
 */
@Slf4j
@Singleton
public class CheckerCacheLoader extends CacheLoader<String, Checker> {

    @Inject
    private ValidatorEngine validatorEngine;

    @Inject
    private Set<Class<? extends Checker>> implementations;

    @Override
    public Checker load(String key) throws Exception {
        try {
            for (Class cls : implementations) {
                try {
                    for (String extension : ((CheckerInfo) cls.getAnnotation(CheckerInfo.class)).value()) {
                        if (key.toLowerCase().endsWith(extension)) {
                            log.debug("Checker '{}'", key);
                            Checker checker = (Checker) cls.getConstructor().newInstance();
                            checker.prepare(validatorEngine.getResource(key));
                            return checker;
                        }
                    }
                } catch (Exception e) {
                    throw new ValidatorException(String.format("Unable to use %s for checker.", cls), e);
                }
            }
        } catch (Exception e) {
            throw new ValidatorException(String.format("Unable to load checker for '%s'.", key), e);
        }

        throw new ValidatorException(String.format("No checker found for '%s'", key));
    }
}
