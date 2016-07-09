package no.difi.vefa.validator.controller;

import no.difi.vefa.validator.service.PiwikService;
import no.difi.vefa.validator.service.WorkspaceService;
import no.difi.xsd.vefa.validator._1.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping(value = "/v/{identifier}", method = RequestMethod.GET)
public class ValidationController {

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private PiwikService piwikService;

    @RequestMapping
    public String present(@PathVariable String identifier, ModelMap modelMap) throws Exception {
        if (!workspaceService.exists(identifier))
            throw new Exception("Workspace not found.");

        Report report = workspaceService.getReport(identifier);

        modelMap.put("identifier", identifier);
        modelMap.put("report", report);

        piwikService.update(modelMap);

        return report.getReport().isEmpty() ? presentSingle(identifier, modelMap) : presentNested(identifier, report, modelMap);
    }

    private String presentSingle(String identifier, ModelMap modelMap) throws Exception {
        modelMap.put("viewExists", workspaceService.getView(identifier).exists());

        return "validation";
    }

    private String presentNested(String identifier, Report report, ModelMap modelMap) throws Exception {
        Set<Report> reports = walkReports(report);

        Map<String, Boolean> views = new HashMap<>();
        for (Report r : reports)
            views.put(r.getUuid(), workspaceService.getView(identifier, r.getUuid()).exists());

        modelMap.put("reports", reports);
        modelMap.put("views", views);

        return "nestedvalidation";
    }

    private Set<Report> walkReports(Report report) {
        Set<Report> reports = new HashSet<>();
        reports.add(report);

        for (Report r : report.getReport())
            reports.addAll(walkReports(r));

        return reports;
    }

    @ResponseBody
    @RequestMapping(value = "/report.json", produces = "application/json")
    public FileSystemResource presentJson(@PathVariable String identifier, HttpServletResponse response) throws Exception {
        if (!workspaceService.exists(identifier))
            throw new Exception("Workspace not found.");

        response.addHeader("Content-Encoding", "gzip");

        return new FileSystemResource(workspaceService.getReportJson(identifier));
    }

    @ResponseBody
    @RequestMapping(value = "/report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource presentXml(@PathVariable String identifier, HttpServletResponse response) throws Exception {
        if (!workspaceService.exists(identifier))
            throw new Exception("Workspace not found.");

        response.addHeader("Content-Disposition", "attachment; filename=\"" + identifier + "-report.xml\"");
        response.addHeader("Content-Encoding", "gzip");

        return new FileSystemResource(workspaceService.getReportXml(identifier));
    }

    @ResponseBody
    @RequestMapping(value = "/source", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource presentSource(@PathVariable String identifier, HttpServletResponse response) throws Exception {
        if (!workspaceService.exists(identifier))
            throw new Exception("Workspace not found.");

        response.addHeader("Content-Disposition", "attachment; filename=\"" + identifier + ".xml\"");
        response.addHeader("Content-Encoding", "gzip");

        return new FileSystemResource(workspaceService.getSource(identifier));
    }

    @ResponseBody
    @RequestMapping(value = "/view", produces = MediaType.TEXT_HTML_VALUE + "; charset=utf-8")
    public FileSystemResource presentView(@PathVariable String identifier) throws Exception {
        if (!workspaceService.exists(identifier))
            throw new Exception("Workspace not found.");
        if (!workspaceService.getView(identifier).exists())
            throw new Exception("Presentation not found.");

        return new FileSystemResource(workspaceService.getView(identifier));
    }

    @ResponseBody
    @RequestMapping(value = "/view/{uuid}", produces = MediaType.TEXT_HTML_VALUE + "; charset=utf-8")
    public FileSystemResource presentView(@PathVariable String identifier, @PathVariable String uuid) throws Exception {
        if (!workspaceService.exists(identifier))
            throw new Exception("Workspace not found.");
        if (!workspaceService.getView(identifier, uuid).exists())
            throw new Exception("Presentation not found.");

        return new FileSystemResource(workspaceService.getView(identifier, uuid));
    }
}
