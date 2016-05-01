package no.difi.vefa.validator.build;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleProjectTest {

    @Test
    public void simple() throws Exception {
        Path path = Paths.get(getClass().getResource("/project/simple").toURI());

        // Assert.assertFalse(Files.exists(path.resolve("target")));

        Assert.assertEquals(Cli.perform(path.toString()), 0);

        Assert.assertTrue(Files.exists(path.resolve("target")));
    }

    @Test
    public void simpleWithTests() throws Exception {
        Path path = Paths.get(getClass().getResource("/project/simple").toURI());

        // Assert.assertFalse(Files.exists(path.resolve("target")));

        Assert.assertEquals(Cli.perform("-test", "-x", path.toString()), 0);

        Assert.assertTrue(Files.exists(path.resolve("target")));
    }
}
