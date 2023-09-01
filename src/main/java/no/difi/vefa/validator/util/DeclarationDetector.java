package no.difi.vefa.validator.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.lang.ValidatorException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Singleton
public class DeclarationDetector {

    public static final DeclarationIdentifier UNKNOWN =
            new DeclarationIdentifier(null, null, Collections.singletonList("unknown"));

    private final List<DeclarationWrapper> rootDeclarationWrappers = new ArrayList<>();

    @Inject
    public DeclarationDetector(List<Declaration> declarations) {
        Map<String, DeclarationWrapper> wrapperMap = new HashMap<>();

        for (Declaration declaration : declarations) {
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

    public DeclarationIdentifier detect(InputStream contentStream) throws IOException {
        return detect(rootDeclarationWrappers, null, contentStream, UNKNOWN);
    }

    private DeclarationIdentifier detect(List<DeclarationWrapper> wrappers, byte[] content, InputStream contentStream,
                                         DeclarationIdentifier parent) throws IOException {

        if (content == null) {
            content = StreamUtils.readAndReset(contentStream, 10 * 1024);
        }

        for (DeclarationWrapper wrapper : wrappers) {
            try {
                if (wrapper.verify(content, parent == null ? null : parent.getIdentifier())) {
                    contentStream.mark(0);
                    List<String> identifier = wrapper.detect(contentStream,
                            parent == null ? null : parent.getIdentifier());

                    if (identifier == null)
                        break;
                    log.debug("Found: {} - {}", wrapper.getType(), identifier);

                    return detect(wrapper.getChildren(), content, contentStream,
                            new DeclarationIdentifier(parent, wrapper, identifier));
                }
            } catch (ValidatorException e) {
                log.warn(e.getMessage(), e);
            } finally {
                try {
                    contentStream.reset();
                } catch (IOException e) {
                    log.warn("Couldn't reset stream!", e);
                }
            }
        }

        return parent;
    }
}
