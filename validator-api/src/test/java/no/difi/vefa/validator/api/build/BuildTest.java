package no.difi.vefa.validator.api.build;

import no.difi.vefa.validator.api.Validation;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildTest {

    @Test
    public void simple() throws Exception {
        Path path = Paths.get(getClass().getResource("/projectFolder").toURI());
        Build build = new Build(path);
        build.setSetting("name", "Test");

        Assert.assertNotNull(build.getConfigurations());
        Assert.assertEquals(build.getConfigurations().getName(), "Test");

        Assert.assertEquals(build.getProjectPath(), path);
        Assert.assertEquals(build.getTargetFolder(), path.resolve("target"));

        Assert.assertEquals(build.getTestFolders().size(), 0);
        build.addTestFolder(path.resolve("testFolder").toFile());
        Assert.assertEquals(build.getTestFolders().size(), 1);
        Assert.assertEquals(build.getTestFolders().get(0), path.resolve("testFolder"));

        Assert.assertEquals(build.getTestValidations().size(), 0);
        build.addTestValidation(Mockito.mock(Validation.class));
        Assert.assertEquals(build.getTestValidations().size(), 1);
    }
}
