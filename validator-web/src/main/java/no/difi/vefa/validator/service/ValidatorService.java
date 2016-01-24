package no.difi.vefa.validator.service;

import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.DirectorySource;
import no.difi.vefa.validator.source.RepositorySource;
import no.difi.xsd.vefa.validator._1.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ValidatorService {

    private static Logger logger = LoggerFactory.getLogger(ValidatorService.class);

    @Autowired
    private PropertiesFactoryBean propertiesFactoryBean;
    @Autowired
    private WorkspaceService workspaceService;

    @Value("${source}")
    private String propSource;
    @Value("${repository}")
    private String propRepository;
    @Value("${directory}")
    private String dirRules;

    private Validator validator;

    @PostConstruct
    public void postConstruct() {
        try {
            Source source;

            switch (propSource) {
                case "directory":
                    List<Path> paths = new ArrayList<>();
                    for (String dir : dirRules.split(";"))
                        paths.add(Paths.get(dir));

                    source = new DirectorySource(paths.toArray(new Path[paths.size()]));
                    break;
                case "repository":
                    source = new RepositorySource(propRepository);
                    break;
                default:
                    throw new Exception("Type of source not recognized.");
            }

            SimpleProperties config = new SimpleProperties();
            List<Map.Entry<Object, Object>> entries = new ArrayList<>(propertiesFactoryBean.getObject().entrySet());
            for (Map.Entry<Object, Object> entry : entries)
                if (String.valueOf(entry.getKey()).startsWith("validator."))
                    config.set(entry.getKey().toString().substring(10), entry.getValue());

            validator = ValidatorBuilder
                    .newValidator()
                    .setProperties(config)
                    .setSource(source)
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String validateWorkspace(InputStream inputStream) throws Exception {
        Validation validation = validator.validate(inputStream);
        return workspaceService.saveValidation(validation);
    }

    public List<PackageType> getPackages() {
        return validator.getPackages();
    }
}
