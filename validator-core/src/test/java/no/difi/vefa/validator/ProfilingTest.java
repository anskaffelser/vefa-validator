package no.difi.vefa.validator;


import no.difi.vefa.validator.api.Validation;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class ProfilingTest {

    private Validator validator;

    @BeforeClass
    public void beforeClass() throws Exception {
        validator = ValidatorBuilder.newValidator().build();
    }

    @Test(enabled = false)
    public void simple() throws Exception {
        for (int i = 0; i < 2000; i++) {
            InputStream inputStream = getClass().getResourceAsStream("/documents/huge-001.xml.gz");
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

            Validation validation = validator.validate(gzipInputStream);
            Assert.assertEquals(FlagType.ERROR, validation.getReport().getFlag());

            gzipInputStream.close();
            inputStream.close();

            System.out.println(i);
        }
    }
}
