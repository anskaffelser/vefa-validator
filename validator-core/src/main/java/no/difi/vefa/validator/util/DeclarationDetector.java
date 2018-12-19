package no.difi.vefa.validator.util;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.lang.ValidatorException;

import java.util.*;

@Slf4j
@Singleton
public class DeclarationDetector {

    public static final DeclarationIdentifier UNKNOWN =
            new DeclarationIdentifier(null, null, Collections.singletonList("unknown"));

    private List<DeclarationWrapper> rootDeclarationWrappers = new ArrayList<>();

    public DeclarationDetector() {
        Map<String, DeclarationWrapper> wrapperMap = new HashMap<>();

        for (Declaration declaration : ServiceLoader.load(Declaration.class)) {
            if (declaration.getClass().isAnnotationPresent(Type.class)) {
                for (String type : declaration.getClass().getAnnotation(Type.class).value()) {
                    wrapperMap.put(type, DeclarationWrapper.of(type, declaration));
                }
            }
        }

        for (String key : wrapperMap.keySet()) {
            if (key.contains(".")) {
                String parent = key.substring(0, key.lastIndexOf("."));
                wrapperMap.get(parent).getChildren().add(wrapperMap.get(key));
            } else {
                rootDeclarationWrappers.add(wrapperMap.get(key));
            }
        }
    }

    public DeclarationIdentifier detect(byte[] content) {
        return detect(rootDeclarationWrappers, content, UNKNOWN);
    }

    private DeclarationIdentifier detect(List<DeclarationWrapper> wrappers, byte[] content,
                                         DeclarationIdentifier parent) {
        for (DeclarationWrapper wrapper : wrappers) {
            try {
                if (wrapper.verify(content, parent == null ? null : parent.getIdentifier())) {
                    List<String> identifier = wrapper.detect(content,
                            parent == null ? null : parent.getIdentifier());
                    if (identifier == null)
                        break;
                    log.debug("Found: {} - {}", wrapper.getType(), identifier);

                    return detect(wrapper.getChildren(), content,
                            new DeclarationIdentifier(parent, wrapper, identifier));
                }
            } catch (ValidatorException e) {
                log.warn(e.getMessage(), e);
            }
        }

        return parent;
    }
}
