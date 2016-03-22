package no.difi.vefa.validator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.Report;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class WorkspaceService {

    private static Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

    @Value("${workspace}")
    private String dirWorkspace;
    @Value("${workspace.expire}")
    private int workspaceExpire;

    static final JAXBContext jaxbContext = initContext();

    private static JAXBContext initContext() {
        try {
            return JAXBContext.newInstance(Report.class);
        } catch(Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    protected File getFolder(String identifier) {
        return new File(dirWorkspace, identifier);
    }


    @SuppressWarnings("all")
    public String saveValidation(Validation validation) throws Exception {
        String identifier = UUID.randomUUID().toString();

        Marshaller reportMarshaller=null;
        File folder=null;
        ObjectMapper objectMapper=null;

        try {
            reportMarshaller = jaxbContext.createMarshaller();
            objectMapper= new ObjectMapper();

            folder = getFolder(identifier);
            folder.mkdirs();

            FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, "source.xml.gz"));
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            IOUtils.copy(validation.getDocument().getInputStream(), gzipOutputStream);
            gzipOutputStream.close();
            fileOutputStream.close();

            fileOutputStream = new FileOutputStream(new File(folder, "report.xml.gz"));
            gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            reportMarshaller.marshal(validation.getReport(), gzipOutputStream);
            gzipOutputStream.close();
            fileOutputStream.close();

            fileOutputStream = new FileOutputStream(new File(folder, "report.json.gz"));
            gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            objectMapper.writeValue(gzipOutputStream, validation.getReport());
            gzipOutputStream.close();
            fileOutputStream.close();

            if (validation.isRenderable()) {
                fileOutputStream = new FileOutputStream(new File(folder, "view.html"));
                validation.render(fileOutputStream);
                fileOutputStream.close();
            }
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
        } catch (ValidatorException e) {
            logger.warn(String.format("%s: %s", identifier, e.getMessage()));
        } catch (Exception e) {
            logger.warn(String.format("%s: %s", identifier, e.getMessage()), e);
        } finally{
            reportMarshaller=null;
            folder=null;
            objectMapper=null;
        }

        return identifier;
    }

    public boolean exists(String identifier) {
        return getFolder(identifier).isDirectory();
    }

    public Report getReport(String identifier) throws Exception {
        InputStream inputStream = null;
        GZIPInputStream gzipInputStream = null;
        Unmarshaller reportUnmarshaller = null;
        Report report = null;

        try {
            reportUnmarshaller = jaxbContext.createUnmarshaller();
            inputStream = new FileInputStream(getReportXml(identifier));
            gzipInputStream = new GZIPInputStream(inputStream);
            report = (Report) reportUnmarshaller.unmarshal(gzipInputStream);
        }catch (Exception e){
            logger.error(e.getMessage(), e);

        } finally {
            inputStream.close();
            gzipInputStream.close();
        }

        return report;
    }

    public File getReportJson(String identifier) {
        return new File(getFolder(identifier), "report.json.gz");
    }

    public File getReportXml(String identifier) {
        return new File(getFolder(identifier), "report.xml.gz");
    }

    public File getSource(String identifier) {
        return new File(getFolder(identifier), "source.xml.gz");
    }

    public File getView(String identifier) {
        return new File(getFolder(identifier), "view.html");
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 1000)
    public void cleanWorkspace() {
        logger.info("Cleaning workspace.");

        if (workspaceExpire < 0)
            return;

        for (File f : new File(dirWorkspace).listFiles()) {
            if (f.isDirectory() && !new File(f, ".keep").exists()) {
                if (new DateTime(f.lastModified()).isBefore(DateTime.now().minusDays(workspaceExpire))) {
                    try {
                        logger.info(String.format("Delete validation '%s'.", f.getName()));
                        FileUtils.deleteDirectory(f);
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
