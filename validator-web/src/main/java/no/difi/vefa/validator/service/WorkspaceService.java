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
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class WorkspaceService {

    private static Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(Report.class);
        } catch (JAXBException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Value("${workspace}")
    private String dirWorkspace;
    @Value("${workspace.expire}")
    private int workspaceExpire;

    protected File getFolder(String identifier) {
        return new File(dirWorkspace, identifier);
    }

    @SuppressWarnings("all")
    public String saveValidation(Validation validation) throws Exception {
        String identifier = validation.getReport().getUuid();

        try {
            File folder = getFolder(validation.getReport().getUuid());
            folder.mkdirs();

            FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, "source.xml.gz"));
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            IOUtils.copy(validation.getDocument().getInputStream(), gzipOutputStream);
            gzipOutputStream.close();
            fileOutputStream.close();

            fileOutputStream = new FileOutputStream(new File(folder, "report.xml.gz"));
            gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            jaxbContext.createMarshaller().marshal(validation.getReport(), gzipOutputStream);
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

            walkRendering(folder, validation);
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
        } catch (ValidatorException e) {
            logger.warn(String.format("%s: %s", identifier, e.getMessage()));
        } catch (Exception e) {
            logger.warn(String.format("%s: %s", identifier, e.getMessage()), e);
        }

        return identifier;
    }

    private void walkRendering(File folder, Validation validation) {
        try {
            if (validation.isRenderable()) {
                FileOutputStream fileOutputStream = new FileOutputStream(new File(folder, String.format("view-%s.html", validation.getReport().getUuid())));
                validation.render(fileOutputStream);
                fileOutputStream.close();
            }
        } catch (Exception e) {
            logger.warn(String.format("%s: %s", validation.getReport().getUuid(), e.getMessage()));
        }

        if (validation.getChildren() != null)
            for (Validation v : validation.getChildren())
                walkRendering(folder, v);
    }

    public boolean exists(String identifier) {
        return getFolder(identifier).isDirectory();
    }

    public Report getReport(String identifier) throws Exception {
        InputStream inputStream = new FileInputStream(getReportXml(identifier));
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        Report report = jaxbContext.createUnmarshaller().unmarshal(new StreamSource(gzipInputStream), Report.class).getValue();
        gzipInputStream.close();
        inputStream.close();

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

    public File getView(String identifier, String uuid) {
        return new File(getFolder(identifier), String.format("view-%s.html", uuid));
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
