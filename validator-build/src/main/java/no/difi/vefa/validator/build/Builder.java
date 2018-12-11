package no.difi.vefa.validator.build;

import lombok.extern.slf4j.Slf4j;
import no.difi.asic.*;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.api.build.Preparer;
import no.difi.vefa.validator.util.JAXBHelper;
import no.difi.xsd.vefa.validator._1.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class Builder {

    private static JAXBContext jaxbContext = JAXBHelper.context(Configurations.class, BuildConfigurations.class);
    private static AsicWriterFactory asicWriterFactory = AsicWriterFactory.newFactory(SignatureMethod.CAdES);

    private GenericKeyedObjectPool<String, Preparer> preparerPool = new GenericKeyedObjectPool<>(new PreparerPoolFactory());

    public void clean(Path targetFolder) throws IOException {
        if (targetFolder.toFile().isDirectory())
            FileUtils.deleteDirectory(targetFolder.toFile());
    }

    protected void prepareTargetFolder(Path targetFolder) throws Exception {
        if (!targetFolder.toFile().mkdir())
            throw new Exception("Unable to make target directory.");
    }

    /**
     * @param signatureHelper SignatureHelper from ASiC library. Set to null to use the self-signed certificate.
     * @throws Exception
     */
    public void build(Build build, SignatureHelper signatureHelper) throws Exception {
        if (signatureHelper == null)
            signatureHelper = new SignatureHelper(Cli.class.getResourceAsStream("/keystore-self-signed.jks"), "changeit", null, "changeit");

        clean(build.getTargetFolder());
        prepareTargetFolder(build.getTargetFolder());

        Configurations configurations = build.getConfigurations();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        AsicWriter asicWriter = asicWriterFactory.newContainer(new File(build.getTargetFolder().toFile(), String.format("%s-%s.asice", build.getSetting("name"), build.getSetting("build"))));

        Set<String> capabilities = new TreeSet<>();

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

                            Preparer preparer = preparerPool.borrowObject(extension);

                            ByteArrayOutputStream byteArrayOutputStream = preparer.prepare(build, new File(configFolder, fileType.getSource()));
                            asicWriter.add(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), fileType.getPath(), MimeType.forString("application/xml"));

                            fileType.setSource(null);

                            preparerPool.returnObject(extension, preparer);
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
