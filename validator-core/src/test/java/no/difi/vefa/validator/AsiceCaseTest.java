package no.difi.vefa.validator;

import com.google.common.jimfs.Jimfs;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.FileSystem;
import java.util.List;

public class AsiceCaseTest {

    @Test
    public void simple() throws Exception {
        Validator validator = ValidatorBuilder
                .newValidator()
                .setSource(new Source() {
                    @Override
                    public SourceInstance createInstance(Properties properties) throws ValidatorException {
                        return new SourceInstance() {
                            @Override
                            public FileSystem getFileSystem() {
                                return Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix());
                            }

                            @Override
                            public List<String> getConfigs() {
                                return null;
                            }
                        };
                    }
                })
                .setProperties(new SimpleProperties()
                                .set("feature.expectation", true)
                                .set("feature.nesting", true)
                )
                .build();

        Validation validation = validator.validate(getClass().getResourceAsStream("/documents/asic-xml.xml"));
        Assert.assertEquals(validation.getReport().getFlag(), FlagType.OK);
        Assert.assertEquals(validation.getChildren().size(), 3);
    }
}
