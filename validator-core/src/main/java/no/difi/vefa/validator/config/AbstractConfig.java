package no.difi.vefa.validator.config;

import no.difi.vefa.validator.api.Config;

abstract class AbstractConfig implements Config {

    @Override
    public Object get(String key) {
        return get(key, null);
    }

    @Override
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

}
