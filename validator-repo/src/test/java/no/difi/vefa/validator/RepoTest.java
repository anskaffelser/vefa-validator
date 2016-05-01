package no.difi.vefa.validator;

import no.difi.xsd.vefa.validator._1.ArtifactType;
import no.difi.xsd.vefa.validator._1.Artifacts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RepoTest {

    private static Logger logger = LoggerFactory.getLogger(RepoTest.class);

    @Test
    public void simpleConstructor() {
        new Repo();
    }

    @Test
    public void simple() throws Exception {
        Path path = Paths.get(getClass().getResource("/simple").toURI());
        Artifacts artifacts = Repo.generateArtifacts(path, false);

        for (ArtifactType artifactType : artifacts.getArtifact())
            logger.info(artifactType.getFilename());

        Assert.assertEquals(artifacts.getArtifact().size(), 2);
        Assert.assertEquals(artifacts.getArtifact().get(0).getFilename(), "ehf-981a3bd21937e9470ecd6da73c628d5116002a86.asice");
    }
}
