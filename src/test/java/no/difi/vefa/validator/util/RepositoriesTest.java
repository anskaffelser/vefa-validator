package no.difi.vefa.validator.util;

import no.difi.vefa.validator.lang.ValidatorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class RepositoriesTest {

    @Test
    public void verifyTestRepo() throws IOException, ValidatorException {
        var repo = Repositories.test();

        Assert.assertNotNull(repo.fetchListing());
    }

    @Test
    public void verifyProductionRepo() throws IOException, ValidatorException {
        var repo = Repositories.production();

        Assert.assertNotNull(repo.fetchListing());

        try (var file = repo.fetch("non-existing.file")) {
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e instanceof FileNotFoundException);
        }
    }

    @Test
    public void verifyClasspathRepo() throws IOException, ValidatorException {
        var repo = Repositories.classpath("/rules");

        Assert.assertNotNull(repo.fetchListing());

        try (var file = repo.fetch("non-existing.file")) {
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e instanceof FileNotFoundException);
        }
    }

    @Test
    public void verifyDirectoryRepo() throws IOException, ValidatorException {
        var repo = Repositories.folder("src/test/resources/rules");

        Assert.assertNotNull(repo.fetchListing());
        Assert.assertEquals(repo.listingFallback().size(), 4);

        try (var file = repo.fetch("non-existing.file")) {
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e instanceof FileNotFoundException);
        }
    }
}
