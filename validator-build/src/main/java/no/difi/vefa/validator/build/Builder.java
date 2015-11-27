package no.difi.vefa.validator.build;

import no.difi.asic.*;
import no.difi.vefa.validator.build.api.Build;
import no.difi.vefa.validator.build.api.Preparer;
import no.difi.xsd.vefa.validator._1.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Path;

public class Builder {

    private static Logger logger = LoggerFactory.getLogger(Builder.class);

    private static JAXBContext jaxbContext;
    private static AsicWriterFactory asicWriterFactory = AsicWriterFactory.newFactory(SignatureMethod.CAdES);

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Configurations.class, BuildConfigurations.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

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
        File workFolder = build.getProjectPath().toFile();

        logger.info(String.format("Using folder: %s", workFolder.getAbsolutePath()));

        clean(build.getTargetFolder());
        prepareTargetFolder(build.getTargetFolder());

        Configurations configurations = build.getConfigurations();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        AsicWriter asicWriter = asicWriterFactory.newContainer(new File(build.getTargetFolder().toFile(), String.format("%s-%s.asice", build.getSetting("name"), build.getSetting("build"))));

        // Find configurations
        for (File file : FileUtils.listFiles(workFolder, new NameFileFilter("buildconfig.xml"), TrueFileFilter.INSTANCE)) {
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

                    asicWriter.add(new File(configFolder, fileType.getSource()), fileType.getPath(), MimeType.forString("something"));
                }

                for (PackageType pkg : config.getPackage())
                    configurations.getPackage().add(pkg);

                for (String testFolder : config.getTestfolder())
                    build.addTestFolder(new File(configFolder, testFolder));

                logger.info(String.format("Loading: %s", file.toString()));
            } catch (JAXBException e) {
                logger.warn(String.format("Failed: %s", file.toString()));
                e.printStackTrace();
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
