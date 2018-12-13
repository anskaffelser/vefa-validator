package no.difi.vefa.validator.build;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.asic.*;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.api.build.Preparer;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Singleton
public class Builder {

    private static JAXBContext jaxbContext = JAXBHelper.context(Configurations.class, BuildConfigurations.class);

    private static AsicWriterFactory asicWriterFactory = AsicWriterFactory.newFactory(SignatureMethod.CAdES);

    @Inject
    private PreparerProvider preparerProvider;

    public void build(final Build build) throws Exception {
        SignatureHelper signatureHelper = new SignatureHelper(Cli.class.getResourceAsStream("/keystore-self-signed.jks"), "changeit", null, "changeit");

        if (Files.exists(build.getTargetFolder()))
            DirectoryCleaner.clean(build.getTargetFolder());
        else
            Files.createDirectories(build.getTargetFolder());

        Configurations configurations = build.getConfigurations();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        AsicWriter asicWriter = asicWriterFactory.newContainer(new File(build.getTargetFolder().toFile(), String.format("%s-%s.asice", build.getSetting("name"), build.getSetting("build"))));

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

                            Preparer preparer = preparerProvider.get(extension);

                            ByteArrayOutputStream byteArrayOutputStream = preparer.prepare(build, new File(configFolder, fileType.getSource()));
                            asicWriter.add(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), fileType.getPath(), MimeType.forString("application/xml"));

                            fileType.setSource(null);
                        }

                        if (configuration.getStylesheet() != null) {
                            StylesheetType stylesheet = configuration.getStylesheet();
                            asicWriter.add(new File(configFolder, stylesheet.getSource() != null ? stylesheet.getSource() : stylesheet.getPath()), stylesheet.getPath(), MimeType.forString("application/xml"));
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

                        asicWriter.add(new File(configFolder, fileType.getSource()), fileType.getPath(), MimeType.forString("application/xml"));
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

        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(configurations, outputStream);
        asicWriter.add(new ByteArrayInputStream(outputStream.toByteArray()), String.format("config-%s-%s.xml", build.getSetting("name"), build.getSetting("build")));
        asicWriter.sign(signatureHelper);
    }
}
