package no.difi.vefa.validator.build.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.api.Preparer;

import java.util.List;

/**
 * @author erlend
 */
@Singleton
public class PreparerProvider {

    @Inject
    private List<Preparer> preparers;

    public Preparer get(String extension) throws ValidatorException {
        for (Preparer preparer : preparers)
            for (String e : preparer.getClass().getAnnotation(Type.class).value())
                if (e.equals(extension))
                    return preparer;

        throw new ValidatorException(String.format("No preparer found for '%s'", extension));
    }
}
