package no.difi.vefa.validator.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Props {

    public static Props init() {
        return new Props();
    }

    private final Map<String, Prop> map;

    private Props() {
        map = Collections.emptyMap();
    }

    private Props(Map<String, Prop> updated) {
        map = updated;
    }

    public String getString(String key, String defaultValue) {
        return map.containsKey(key) ? map.get(key).asString() : defaultValue;
    }

    public String getString(String key, Supplier<String> defaultSupplier) {
        return map.containsKey(key) ? map.get(key).asString() : defaultSupplier.get();
    }

    public int getInt(String key, int defaultValue) {
        return map.containsKey(key) ? map.get(key).asInt() : defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        return map.containsKey(key) ? map.get(key).asLong() : defaultValue;
    }

    public boolean getBool(String key, boolean defaultValue) {
        return map.containsKey(key) ? map.get(key).asBool() : defaultValue;
    }

    public Props update(Prop... props) {
        if (props == null || props.length == 0)
            return this;

        Map<String, Prop> updated = new HashMap<>(map);

        for (var prop : props)
            updated.put(prop.getKey(), prop);

        return new Props(updated);
    }
}
