package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Presenter;
import no.difi.vefa.validator.api.PresenterInfo;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.presenter.XsltPresenter;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

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
            StylesheetType stylesheetType = validatorEngine.getStylesheet(key);

            for (Class cls : implementations) {
                try {
                    for (String extension : ((PresenterInfo) cls.getAnnotation(PresenterInfo.class)).value()) {
                        if (stylesheetType.getPath().toLowerCase().endsWith(extension)) {
                            Presenter presenter = (Presenter) cls.getConstructor().newInstance();
                            presenter.prepare(stylesheetType, validatorEngine.getResource(stylesheetType.getPath()));
                            return presenter;
                        }
                    }
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
