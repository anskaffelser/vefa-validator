package no.difi.vefa.validator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.util.JAXBHelper;
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
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class WorkspaceService {

    private static Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JAXBContext jaxbContext = JAXBHelper.context(Report.class);

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

            try (
                    OutputStream targetStream = new FileOutputStream(new File(folder, "source.xml.gz"));
                    OutputStream outputStream = new GZIPOutputStream(targetStream);
            ) {
                IOUtils.copy(validation.getDocument().getInputStream(), outputStream);
            }

            try (
                    OutputStream targetStream = new FileOutputStream(new File(folder, "report.xml.gz"));
                    OutputStream outputStream = new GZIPOutputStream(targetStream);
            ) {
                jaxbContext.createMarshaller().marshal(validation.getReport(), outputStream);
            }

            try (
                    OutputStream targetStream = new FileOutputStream(new File(folder, "report.json.gz"));
                    OutputStream outputStream = new GZIPOutputStream(targetStream);
            ) {
                objectMapper.writeValue(outputStream, validation.getReport());
            }

            if (validation.isRenderable()) {
                try (OutputStream outputStream = new FileOutputStream(new File(folder, "view.html"))) {
                    validation.render(outputStream);
                }
            }

            walkRendering(folder, validation);
        } catch (ValidatorException e) {
            logger.warn(String.format("%s: %s", identifier, e.getMessage()));
        }

        return identifier;
    }

    private void walkRendering(File folder, Validation validation) {
        try {
            if (validation.isRenderable()) {
                String filename = String.format("view-%s.html", validation.getReport().getUuid());
                try (OutputStream outputStream = new FileOutputStream(new File(folder, filename))) {
                    validation.render(outputStream);
                }
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
        try (
                InputStream sourceStream = new FileInputStream(getReportXml(identifier));
                InputStream inputStream = new GZIPInputStream(sourceStream);
        ) {
            return jaxbContext.createUnmarshaller().unmarshal(new StreamSource(inputStream), Report.class).getValue();
        }
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
                        logger.info("Delete validation '{}'.", f.getName());
                        FileUtils.deleteDirectory(f);
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
