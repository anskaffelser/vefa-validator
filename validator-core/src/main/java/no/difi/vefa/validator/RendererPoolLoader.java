package no.difi.vefa.validator;

import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Renderer;
import no.difi.vefa.validator.api.RendererInfo;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.StylesheetType;

/**
 * Pool of prepared renderers. Size if configured using properties.
 */
@Slf4j
class RendererPoolLoader extends CacheLoader<String, Renderer> {

    private ValidatorEngine validatorEngine;
    private Class<? extends Renderer>[] implementations;

    RendererPoolLoader(ValidatorEngine validatorEngine, Class<? extends Renderer>[] implementations) {
        this.validatorEngine = validatorEngine;
        this.implementations = implementations;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Renderer load(String key) throws Exception {
        try {
            StylesheetType stylesheetType = validatorEngine.getStylesheet(key);

            for (Class cls : implementations) {
                try {
                    for (String extension : ((RendererInfo) cls.getAnnotation(RendererInfo.class)).value()) {
                        if (stylesheetType.getPath().toLowerCase().endsWith(extension)) {
                            log.debug("Renderer '{}'", key);
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
}
