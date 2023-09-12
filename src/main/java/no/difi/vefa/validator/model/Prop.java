package no.difi.vefa.validator.model;

public class Prop {

    public static Prop of(String key, String value) {
        return new Prop(key, value);
    }

    public static Prop of(String key, boolean value) {
        return new Prop(key, Boolean.toString(value));
    }

    public static Prop of(String key, int value) {
        return new Prop(key, Integer.toString(value));
    }

    public static Prop of(String key, long value) {
        return new Prop(key, Long.toString(value));
    }

    private final String key;

    private final String value;

    private Prop(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String asString() {
        return value;
    }

    public int asInt() {
        return Integer.parseInt(value);
    }

    public long asLong() {
        return Long.parseLong(value);
    }

    public boolean asBool() {
        return Boolean.parseBoolean(value);
    }
}
