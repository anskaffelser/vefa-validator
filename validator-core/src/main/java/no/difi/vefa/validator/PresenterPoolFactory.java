package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Presenter;
import no.difi.vefa.validator.api.PresenterInfo;
import no.difi.vefa.validator.presenter.XsltPresenter;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.nio.file.Path;

class PresenterPoolFactory extends BaseKeyedPooledObjectFactory<String, Presenter> {

    private static Class[] implementations = new Class[] {
            XsltPresenter.class,
    };

    private ValidatorEngine validatorEngine;

    PresenterPoolFactory(ValidatorEngine validatorEngine) {
        this.validatorEngine = validatorEngine;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Presenter create(String key) throws Exception {
        try {
            for (Class cls : implementations) {
                try {
                    for (String extension : ((PresenterInfo) cls.getAnnotation(PresenterInfo.class)).value())
                        if (key.toLowerCase().endsWith(extension))
                            return (Presenter) cls.getConstructor(Path.class).newInstance(validatorEngine.getResource(key));
                } catch (Exception e) {
                    throw new ValidatorException(String.format("Unable to use %s for presenter.", cls), e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidatorException(String.format("Unable to load presenter for '%s'.", key), e);
        }

        throw new ValidatorException(String.format("No presenter found for '%s'", key));
    }

    @Override
    public PooledObject<Presenter> wrap(Presenter presenter) {
        return new DefaultPooledObject<>(presenter);
    }
}
