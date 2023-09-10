package no.difi.vefa.validator.util;

import java.util.function.Supplier;

public interface FunctionUtil {

    static <T> T load(Supplier<T> supplier) {
        return supplier.get();
    }
}
