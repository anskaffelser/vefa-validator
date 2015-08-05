package no.difi.vefa.validator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class SimpleConfig extends AbstractConfig {

    private static Logger logger = LoggerFactory.getLogger(SimpleConfig.class);

    private HashMap<String, Object> values;

    public SimpleConfig() {
        values = new HashMap<>();
    }

    public SimpleConfig set(String key, Object value) {
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
