package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerInfo;
import no.difi.vefa.validator.api.ValidatorException;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CheckerPoolFactory extends BaseKeyedPooledObjectFactory<String, Checker> {

    private static Logger logger = LoggerFactory.getLogger(CheckerPoolFactory.class);

    private ValidatorEngine validatorEngine;
    private Class<? extends Checker>[] implementations;

    CheckerPoolFactory(ValidatorEngine validatorEngine, Class<? extends Checker>[] implementations) {
        this.validatorEngine = validatorEngine;
        this.implementations = implementations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Checker create(String key) throws Exception {
        try {
            for (Class cls : implementations) {
                try {
                    for (String extension : ((CheckerInfo) cls.getAnnotation(CheckerInfo.class)).value()) {
                        if (key.toLowerCase().endsWith(extension)) {
                            logger.debug(String.format("Creating checker '%s'", key));
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

    @Override
    public PooledObject<Checker> wrap(Checker checker) {
        return new DefaultPooledObject<>(checker);
    }
}
