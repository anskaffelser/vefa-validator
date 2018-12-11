package no.difi.vefa.validator.properties;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of Properties using a HashMap to store values.
 */
@Slf4j
public class SimpleProperties extends AbstractProperties {

    private Map<String, Object> values;

    public SimpleProperties() {
        values = new HashMap<>();
    }

    public SimpleProperties set(String key, Object value) {
        values.put(key, value);
        return this;
    }

    @Override
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    @Override
    public Object get(String key, Object defaultValue) {
        return values.containsKey(key) ? values.get(key) : defaultValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return values.containsKey(key) ? Boolean.parseBoolean(String.valueOf(values.get(key))) : defaultValue;
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        try {
            if (values.containsKey(key))
                return Integer.parseInt(String.valueOf(values.get(key)));
        } catch (NumberFormatException e) {
            log.error(String.format("Error while casting '%s' to integer for key '%s'.", values.get(key), key));
        }
        return defaultValue;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return values.containsKey(key) ? String.valueOf(values.get(key)) : defaultValue;
    }
}
