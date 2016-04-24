package no.difi.vefa.validator;

import com.google.common.jimfs.Jimfs;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Source;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.FileSystem;
import java.util.Set;

public class AsiceCaseTest {

    @Test
    public void simple() throws Exception {
        Validator validator = ValidatorBuilder
                .newValidator()
                .setSource(new Source() {
                    @Override
                    public SourceInstance createInstance(Properties properties, Set<String> capabilities) throws ValidatorException {
                        return new SourceInstance() {
                            @Override
                            public FileSystem getFileSystem() {
                                return Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());
                            }
                        };
                    }
                })
                .setProperties(new SimpleProperties()
                                .set("feature.expectation", true)
                                .set("feature.nesting", true)
                )
                .build();

        Report report = validator.validate(getClass().getResourceAsStream("/documents/asic-xml.xml")).getReport();
        Assert.assertEquals(report.getFlag(), FlagType.OK);
    }
}