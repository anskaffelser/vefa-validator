package no.difi.vefa.validator.build;

import no.difi.asic.*;
import no.difi.vefa.validator.Validation;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.build.api.Preparer;
import no.difi.vefa.validator.source.DirectorySource;
import no.difi.xsd.vefa.validator._1.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Builder {

    private static Logger logger = LoggerFactory.getLogger(Builder.class);

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Configurations.class, BuildConfigurations.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Path path;
    private File targetFolder;

    private List<File> testFolders = new ArrayList<>();

    private Configurations configurations;
    private List<Validation> validations;

    private AsicWriterFactory asicWriterFactory = AsicWriterFactory.newFactory(SignatureMethod.CAdES);

    public Builder(Path path) {
        this.path = path;
        targetFolder = new File(path.toFile(), "target");
    }

    public void clean() throws IOException {
        if (targetFolder.isDirectory())
            FileUtils.deleteDirectory(targetFolder);
    }

    protected void prepareTargetFolder() throws Exception {
        if (!targetFolder.mkdir())
            throw new Exception("Unable to make target directory.");
    }

    /**
     * @param name Build name
     * @param buildIdentifier Build identifier
     * @param weight Build weight
     * @param signatureHelper SignatureHelper from ASiC library. Set to null to use the self-signed certificate.
     * @throws Exception
     */
    public void build(String name, String buildIdentifier, long weight, SignatureHelper signatureHelper) throws Exception {
        GenericKeyedObjectPool<String, Preparer> preparerPool = new GenericKeyedObjectPool<>(new PreparerPoolFactory());

        File workFolder = path.toFile();

        logger.info(String.format("Using folder: %s", workFolder.getAbsolutePath()));

        clean();
        prepareTargetFolder();

        configurations = new Configurations();
        configurations.setName(name);
        configurations.setTimestamp(System.currentTimeMillis());

        testFolders = new ArrayList<>();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        AsicWriter asicWriter = asicWriterFactory.newContainer(new File(targetFolder, String.format("%s-%s.asice", name, buildIdentifier)));

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

                        ByteArrayOutputStream byteArrayOutputStream = preparer.prepare(new File(configFolder, fileType.getSource()));
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
                        configuration.setBuild(buildIdentifier);

                    if (configuration.getWeight() == 0)
                        configuration.setWeight(weight);

                    configurations.getConfiguration().add(configuration);
                }

                for (FileType fileType : config.getInclude()) {
                    if (fileType.getSource() == null)
                        fileType.setSource(fileType.getPath());

                    asicWriter.add(new File(configFolder, fileType.getSource()), fileType.getPath(), MimeType.forString("something"));
                }

                for (PackageType pkg : config.getPackage())
                    configurations.getPackage().add(pkg);

                for (String testfolder : config.getTestfolder())
                    testFolders.add(new File(configFolder, testfolder));

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
        asicWriter.add(new ByteArrayInputStream(outputStream.toByteArray()), String.format("config-%s-%s.xml", name, buildIdentifier));

        if (signatureHelper == null)
            signatureHelper = new SignatureHelper(Cli.class.getResourceAsStream("/keystore-self-signed.jks"), "changeit", null, "changeit");
        asicWriter.sign(signatureHelper);
    }

    public void test() throws Exception {
        Validator validator = ValidatorBuilder.newValidator().setSource(new DirectorySource(targetFolder.toPath())).build();
        validations = new ArrayList<>();

        for (File testFolder : testFolders) {
            for (File file : FileUtils.listFiles(testFolder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
                try {
                    Validation validation = validator.validate(file);
                    validation.getReport().setFilename(file.toString());
                    validations.add(validation);
                    logger.info(String.format("%s (%s)", file, validation.getReport().getFlag()));
                } catch (Exception e) {
                    logger.warn(String.format("%s (%s)", file, e.getMessage()));
                }
            }
        }
    }

    public void site() throws Exception {
        Site site = new Site(path.toFile(), new File(path.toFile(), "target/site"));
        site.setConfigurations(configurations);
        site.setValidations(validations);
        site.build();
    }
}
