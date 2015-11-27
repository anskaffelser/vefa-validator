package no.difi.vefa.validator.build.api;

import no.difi.vefa.validator.Validation;
import no.difi.xsd.vefa.validator._1.Configurations;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Build {

    private Map<String, String> setting = new HashMap<>();

    private Path projectPath;
    private Path targetFolder;

    private Configurations configurations;

    private List<Path> testFolders = new ArrayList<>();
    private List<Validation> testValidations = new ArrayList<>();

    public Build(Path projectPath) {
        this(projectPath, new File(projectPath.toFile(), "target").toPath());
    }

    public Build(Path projectPath, Path targetFolder) {
        this.projectPath = projectPath;
        this.targetFolder = targetFolder;
    }

    public Configurations getConfigurations() {
        if (configurations == null) {
            configurations = new Configurations();
            configurations.setName(getSetting("name"));
            configurations.setTimestamp(System.currentTimeMillis());
        }

        return configurations;
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public Path getTargetFolder() {
        return targetFolder;
    }

    public void setSetting(String key, String value) {
        setting.put(key, value);
    }

    public String getSetting(String key) {
        return setting.get(key);
    }

    public void addTestFolder(File testFolder) {
        addTestFolder(testFolder.toPath());
    }

    public void addTestFolder(Path testFolder) {
        testFolders.add(testFolder);
    }

    public List<Path> getTestFolders() {
        return testFolders;
    }

    public void addTestValidation(Validation validation) {
        testValidations.add(validation);
    }

    public List<Validation> getTestValidations() {
        return testValidations;
    }
}
