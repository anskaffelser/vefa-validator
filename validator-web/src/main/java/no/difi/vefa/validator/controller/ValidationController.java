package no.difi.vefa.validator.controller;

import no.difi.vefa.validator.service.PiwikService;
import no.difi.vefa.validator.service.WorkspaceService;
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

        modelMap.put("identifier", identifier);
        modelMap.put("report", workspaceService.getReport(identifier));
        modelMap.put("viewExists", workspaceService.getView(identifier).exists());

        piwikService.update(modelMap);
        return "validation";
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
}
