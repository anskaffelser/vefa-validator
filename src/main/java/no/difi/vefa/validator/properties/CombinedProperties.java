package no.difi.vefa.validator.properties;

import no.difi.vefa.validator.api.Properties;

/**
 * Implementation of Properties making it easy to access multiple instances of Properties.
 */
public class CombinedProperties extends AbstractProperties {

    private final Properties[] properties;

    /**
     * Allow combination of configs, the most specific first.
     *
     * @param properties List containing instances of Properties to be combined.
     */
    public CombinedProperties(Properties... properties) {
        this.properties = properties;
    }

    private Properties detect(String key) {
        for (Properties properties : this.properties)
            if (properties != null && properties.contains(key))
                return properties;
        return null;
    }

    @Override
    public boolean contains(String key) {
        Properties properties = detect(key);
        return properties != null;
    }

    @Override
    public Object get(String key, Object defaultValue) {
        Properties properties = detect(key);
        return properties == null ? defaultValue : properties.get(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Properties properties = detect(key);
        return properties == null ? defaultValue : properties.getBoolean(key, defaultValue);
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        Properties properties = detect(key);
        return properties == null ? defaultValue : properties.getInteger(key, defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Properties properties = detect(key);
        return properties == null ? defaultValue : properties.getString(key, defaultValue);
    }
}
