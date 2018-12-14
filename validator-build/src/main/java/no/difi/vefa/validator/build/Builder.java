package no.difi.vefa.validator.build;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Build;
import no.difi.vefa.validator.build.util.AsicArchiver;
import no.difi.vefa.validator.build.util.DirectoryCleaner;
import no.difi.vefa.validator.build.util.PreparerProvider;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Singleton
public class Builder {

    private static final JAXBContext JAXB_CONTEXT = JAXBHelper.context(Configurations.class, BuildConfigurations.class);

    @Inject
    private PreparerProvider preparerProvider;

    public void build(final Build build) throws Exception {
        Path contentsPath = build.getTargetFolder().resolve("contents");

        if (Files.exists(build.getTargetFolder()))
            DirectoryCleaner.clean(build.getTargetFolder(), false);
        Files.createDirectories(contentsPath);

        Configurations configurations = build.getConfigurations();

        Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

        for (Path sourcePath : build.getSourcePath()) {

            File workFolder = sourcePath.toFile();

            log.info("Source '{}'", workFolder.getAbsolutePath());

            // Find configurations
            for (File file : FileUtils.listFiles(workFolder, new NameFileFilter(build.getSetting("config")), TrueFileFilter.INSTANCE)) {
                try {
                    BuildConfigurations config = (BuildConfigurations) unmarshaller.unmarshal(new FileInputStream(file));

                    File configFolder = new File(file.getParent());

                    for (ConfigurationType configuration : config.getConfiguration()) {
                        for (FileType fileType : configuration.getFile()) {
                            if (fileType.getSource() == null)
                                fileType.setSource(fileType.getPath());

                            String extension = fileType.getSource().substring(fileType.getSource().lastIndexOf("."));

                            Path target = contentsPath.resolve(fileType.getPath());
                            Files.createDirectories(target.getParent());
                            preparerProvider.prepare(extension, configFolder.toPath().resolve(fileType.getSource()), target);

                            fileType.setSource(null);
                        }

                        if (configuration.getStylesheet() != null) {
                            StylesheetType stylesheet = configuration.getStylesheet();
                            String source = stylesheet.getSource() != null ? stylesheet.getSource() : stylesheet.getPath();

                            Path target = contentsPath.resolve(stylesheet.getPath());
                            Files.createDirectories(target.getParent());
                            Files.copy(configFolder.toPath().resolve(source), target);

                            stylesheet.setSource(null);
                        }

                        if (configuration.getBuild() == null)
                            configuration.setBuild(build.getSetting("build"));

                        if (configuration.getWeight() == 0)
                            configuration.setWeight(Long.parseLong(build.getSetting("weight")));

                        configurations.getConfiguration().add(configuration);
                    }

                    for (FileType fileType : config.getInclude()) {
                        if (fileType.getSource() == null)
                            fileType.setSource(fileType.getPath());

                        Path target = contentsPath.resolve(fileType.getPath());
                        Files.createDirectories(target.getParent());
                        Files.copy(configFolder.toPath().resolve(fileType.getSource()), target);
                    }

                    configurations.getPackage().addAll(config.getPackage());

                    for (String testFolder : config.getTestfolder())
                        if (testFolder.equals("."))
                            build.addTestFolder(configFolder);
                        else
                            build.addTestFolder(new File(configFolder, testFolder));

                    log.info("Loading '{}'", file.toString());
                } catch (JAXBException e) {
                    log.warn("Loading failed for '{}'", file.toString());
                    e.printStackTrace();
                }
            }
        }

        String configFilename = String.format("config-%s-%s.xml", build.getSetting("name"), build.getSetting("build"));
        try (OutputStream outputStream = Files.newOutputStream(contentsPath.resolve(configFilename))) {
            Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(configurations, outputStream);
        }

        AsicArchiver.archive(
                build.getTargetFolder().resolve(String.format("%s-%s.asice", build.getSetting("name"), build.getSetting("build"))),
                contentsPath);

        DirectoryCleaner.clean(contentsPath, true);
    }
}
