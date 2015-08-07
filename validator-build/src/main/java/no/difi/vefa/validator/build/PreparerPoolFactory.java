package no.difi.vefa.validator.build;

import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.build.api.Preparer;
import no.difi.vefa.validator.build.api.PreparerInfo;
import no.difi.vefa.validator.build.preparer.SchematronPreparer;
import no.difi.vefa.validator.build.preparer.XsdPreparer;
import no.difi.vefa.validator.build.preparer.XsltPreparer;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

class PreparerPoolFactory extends BaseKeyedPooledObjectFactory<String, Preparer> {

    private static Class[] implementations = new Class[] {
            SchematronPreparer.class,
            XsdPreparer.class,
            XsltPreparer.class,
    };

    @SuppressWarnings("unchecked")
    @Override
    public Preparer create(String extension) throws Exception {
        try {
            for (Class cls : implementations) {
                try {
                    for (String e : ((PreparerInfo) cls.getAnnotation(PreparerInfo.class)).value())
                        if (e.equals(extension))
                            return (Preparer) cls.getConstructor().newInstance();
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
