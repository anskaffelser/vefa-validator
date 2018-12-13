package no.difi.vefa.validator.api;

public interface Properties {

    boolean contains(String key);

    Object get(String key);

    Object get(String key, Object defaultValue);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    int getInteger(String key);

    int getInteger(String key, int defaultValue);

    String getString(String key);

    String getString(String key, String defaultValue);
}
