package no.difi.vefa.validator.controller;

import no.difi.vefa.validator.service.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ValidatorService validatorService;

    @RequestMapping
    public String view(ModelMap modelMap) {
        modelMap.put("packages", validatorService.getPackages());

        return "home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());

        if ("application/x-gzip".equals(file.getContentType()))
            inputStream = new GZIPInputStream(inputStream);

        String identifier = validatorService.validateWorkspace(inputStream);

        return "redirect:/v/" + identifier;
    }

}
