package no.difi.vefa.validator.config;

import no.difi.vefa.validator.api.Config;

public class CombinedConfig extends AbstractConfig {

    private Config[] configs;

    /**
     * Allow combination of configs, the most specific first.
     *
     * @param configs
     */
    public CombinedConfig(Config... configs) {
        this.configs = configs;
    }

    private Config detect(String key) {
        for (Config config : configs)
            if (config != null && config.contains(key))
                return config;
        return null;
    }

    @Override
    public boolean contains(String key) {
        Config config = detect(key);
        return config != null;
    }

    @Override
    public Object get(String key, Object defaultValue) {
        Config config = detect(key);
        return config == null ? defaultValue : config.get(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Config config = detect(key);
        return config == null ? defaultValue : config.getBoolean(key, defaultValue);
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        Config config = detect(key);
        return config == null ? defaultValue : config.getInteger(key, defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Config config = detect(key);
        return config == null ? defaultValue : config.getString(key, defaultValue);
    }
}
