package no.difi.vefa.validator.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Simple implementation of Properties using a HashMap to store values.
 */
public class SimpleProperties extends AbstractProperties {

    private static Logger logger = LoggerFactory.getLogger(SimpleProperties.class);

    private HashMap<String, Object> values;

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
            logger.error(String.format("Error while casting '%s' to integer for key '%s'.", values.get(key), key));
        }
        return defaultValue;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return values.containsKey(key) ? String.valueOf(values.get(key)) : defaultValue;
    }
}
