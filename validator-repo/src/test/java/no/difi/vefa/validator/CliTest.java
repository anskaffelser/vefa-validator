package no.difi.vefa.validator;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CliTest {

    @Test
    public void simpleConstructor() {
        new Cli();
    }

    @Test
    public void triggerHelp() throws Exception {
        Cli.main();
    }

    @Test
    public void triggerCreateArtifacts() throws Exception {
        Path path = Paths.get(getClass().getResource("/simple").toURI());
        Path artifactsPath = path.resolve("artifacts.xml");

        Assert.assertFalse(Files.exists(artifactsPath));
        Cli.main("-t", path.toString());
        Assert.assertTrue(Files.exists(artifactsPath));

        Files.delete(artifactsPath);
        Assert.assertFalse(Files.exists(artifactsPath));
    }
}
