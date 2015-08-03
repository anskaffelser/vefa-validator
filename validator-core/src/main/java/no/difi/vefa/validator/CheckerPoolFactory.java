package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Checker;
import no.difi.vefa.validator.api.CheckerInfo;
import no.difi.vefa.validator.checker.XsdChecker;
import no.difi.vefa.validator.checker.XsltChecker;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.nio.file.Path;

class CheckerPoolFactory extends BaseKeyedPooledObjectFactory<String, Checker> {

    private static Class[] implementations = new Class[] {
            XsltChecker.class,
            XsdChecker.class,
    };

    private ValidatorEngine validatorEngine;

    CheckerPoolFactory(ValidatorEngine validatorEngine) {
        this.validatorEngine = validatorEngine;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Checker create(String key) throws Exception {
        try {
            for (Class cls : implementations) {
                try {
                    for (String extension : ((CheckerInfo) cls.getAnnotation(CheckerInfo.class)).value())
                        if (key.toLowerCase().endsWith(extension))
                            return (Checker) cls.getConstructor(Path.class).newInstance(validatorEngine.getResource(key));
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
