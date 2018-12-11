package no.difi.vefa.validator.build;

import com.google.common.reflect.ClassPath;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.api.build.Preparer;
import no.difi.vefa.validator.api.build.PreparerInfo;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class PreparerPoolFactory extends BaseKeyedPooledObjectFactory<String, Preparer> {

    private static List<Class<? extends Preparer>> preparers = new ArrayList<>();

    static {
        try {
            ClassPath classPath = ClassPath.from(PreparerPoolFactory.class.getClassLoader());
            for (ClassPath.ClassInfo info : classPath.getTopLevelClasses("no.difi.vefa.validator.build.preparer")) {
                try {
                    preparers.add((Class<? extends Preparer>) info.load());
                    log.info("Preparer '{}'", info.getName());
                } catch (Exception e) {
                    log.info("Unable to load '{}'", info.getName());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Preparer create(String extension) throws Exception {
        try {
            for (Class<? extends Preparer> cls : preparers) {
                try {
                    for (String e : cls.getAnnotation(PreparerInfo.class).value())
                        if (e.equals(extension))
                            return cls.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new ValidatorException(String.format("Unable to use %s for preparer.", cls), e);
                }
            }
        } catch (Exception e) {
            throw new ValidatorException(String.format("Unable to load preparer for '%s'.", extension), e);
        }

        throw new ValidatorException(String.format("No checker found for '%s'", extension));
    }

    @Override
    public PooledObject<Preparer> wrap(Preparer preparer) {
        return new DefaultPooledObject<>(preparer);
    }
}
