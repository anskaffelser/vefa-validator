package no.difi.vefa.validator.config;

import static org.testng.Assert.*;

import no.difi.vefa.validator.api.Config;
import org.testng.annotations.Test;

public class SimpleConfigTest {

    @Test
    public void simple() {
        Config config = new SimpleConfig()
                .set("some.string", "Hello World!")
                .set("some.number", 1)
                .set("some.boolean", true)
                .set("some.object", new Long(123));

        assertTrue(config.contains("some.string"));
        assertTrue(config.contains("some.number"));
        assertTrue(config.contains("some.boolean"));
        assertTrue(config.contains("some.object"));

        assertFalse(config.contains("some.other.string"));
        assertFalse(config.contains("some.other.number"));
        assertFalse(config.contains("some.other.boolean"));
        assertFalse(config.contains("some.other.object"));

        assertEquals(config.getString("some.string"), "Hello World!");
        assertEquals(config.getInteger("some.number"), 1);
        assertEquals(config.getBoolean("some.boolean"), true);
        assertEquals(config.get("some.object").getClass(), Long.class);

        assertEquals(config.getString("some.string", "No no!"), "Hello World!");
        assertEquals(config.getInteger("some.number", 0), 1);
        assertEquals(config.getBoolean("some.boolean", false), true);
        assertEquals(config.get("some.object", null).getClass(), Long.class);

        assertEquals(config.getString("some.other.string"), null);
        assertEquals(config.getInteger("some.other.number"), 0);
        assertEquals(config.getBoolean("some.other.boolean"), false);
        assertEquals(config.get("some.other.object"), null);

        assertEquals(config.getString("some.other.string", "No no!"), "No no!");
        assertEquals(config.getInteger("some.other.number", 0), 0);
        assertEquals(config.getBoolean("some.other.boolean", true), true);
        assertEquals(config.get("some.other.object", null), null);

        assertEquals(config.getInteger("some.boolean", 100), 100);
    }

}
