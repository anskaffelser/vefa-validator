package no.difi.vefa.validator.service;

import no.difi.vefa.validator.Validation;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.source.DirectorySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ValidatorService {

    private static Logger logger = LoggerFactory.getLogger(ValidatorService.class);

    @Autowired
    private WorkspaceService workspaceService;

    @Value("${dir.rules}")
    private String dirRules;

    private Validator validator;

    @PostConstruct
    public void postConstruct() {
        try {
            validator = ValidatorBuilder.newValidator().setSource(new DirectorySource(Paths.get(dirRules))).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String validateWorkspace(InputStream inputStream) throws Exception {
        Validation validation = validator.validate(inputStream);
        return workspaceService.saveValidation(validation);
    }

    public List<String> getPackages() {
        return validator.getPackages();
    }
}
