package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.api.RendererInfo;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.renderer.XsltRenderer;
import no.difi.xsd.vefa.validator._1.StylesheetType;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RendererPoolFactory extends BaseKeyedPooledObjectFactory<String, Renderer> {

    private static Logger logger = LoggerFactory.getLogger(RendererPoolFactory.class);

    private static Class[] implementations = new Class[] {
            XsltRenderer.class,
    };

    private ValidatorEngine validatorEngine;

    RendererPoolFactory(ValidatorEngine validatorEngine) {
        this.validatorEngine = validatorEngine;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Renderer create(String key) throws Exception {
        try {
            StylesheetType stylesheetType = validatorEngine.getStylesheet(key);

            for (Class cls : implementations) {
                try {
                    for (String extension : ((RendererInfo) cls.getAnnotation(RendererInfo.class)).value()) {
                        if (stylesheetType.getPath().toLowerCase().endsWith(extension)) {
                            logger.debug(String.format("Creating checker '%s'", key));
                            Renderer renderer = (Renderer) cls.getConstructor().newInstance();
                            renderer.prepare(stylesheetType, validatorEngine.getResource(stylesheetType.getPath()));
                            return renderer;
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
    public PooledObject<Renderer> wrap(Renderer renderer) {
        return new DefaultPooledObject<>(renderer);
    }
}
