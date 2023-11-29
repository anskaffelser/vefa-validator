package no.difi.vefa.validator.declaration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.service.DetectorService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UnCefactDeclarationTest {

    @Inject
    private DetectorService declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new ValidatorModule(null)).injectMembers(this);
    }

    @Test
    public void simplePeppol() throws Exception {
        var document = Document.ofResource("/documents/uncefact-peppol.xml");

        Assert.assertEquals(
                declarationDetector.detect(document).getIdentifier().get(0),
                "CrossIndustryInvoice" +
                        "::urn:fdc:peppol.eu:2017:poacc:billing:01:1.0" +
                        "::urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0");
    }

    @Test
    public void simpleTC434() throws Exception {
        var document = Document.ofResource("/documents/uncefact-tc434.xml");

        Assert.assertEquals(
                declarationDetector.detect(document).getIdentifier().get(0),
                "CrossIndustryInvoice::urn:cen.eu:en16931:2017");
    }


    @Test
    public void simpleSimple() throws Exception {
        var document = Document.ofResource("/documents/uncefact-simple.xml");

        Assert.assertEquals(
                declarationDetector.detect(document).getIdentifier().get(0),
                "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice");
    }
}
