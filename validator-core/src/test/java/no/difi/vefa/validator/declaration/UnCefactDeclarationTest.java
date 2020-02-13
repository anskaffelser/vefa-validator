package no.difi.vefa.validator.declaration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import no.difi.vefa.validator.module.SaxonModule;
import no.difi.vefa.validator.module.SbdhModule;
import no.difi.vefa.validator.module.ValidatorModule;
import no.difi.vefa.validator.util.DeclarationDetector;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;

import static no.difi.vefa.validator.util.StreamUtils.readAllAndReset;

public class UnCefactDeclarationTest {

    @Inject
    private DeclarationDetector declarationDetector;

    @BeforeClass
    public void beforeClass() {
        Guice.createInjector(new SaxonModule(), new SbdhModule(), new ValidatorModule())
                .injectMembers(this);
    }

    @Test
    public void simplePeppol() throws Exception {
        InputStream inputStream =  new BufferedInputStream(getClass().getResourceAsStream("/documents/uncefact-peppol.xml"));
        Assert.assertEquals(
                declarationDetector.detect(inputStream).getIdentifier().get(0),
                "CrossIndustryInvoice" +
                        "::urn:fdc:peppol.eu:2017:poacc:billing:01:1.0" +
                        "::urn:cen.eu:en16931:2017#compliant#urn:fdc:peppol.eu:2017:poacc:billing:3.0");
    }

    @Test
    public void simpleTC434() throws Exception {
        InputStream inputStream =  new BufferedInputStream(getClass().getResourceAsStream("/documents/uncefact-tc434.xml"));
        Assert.assertEquals(
                declarationDetector.detect(inputStream).getIdentifier().get(0),
                "CrossIndustryInvoice::urn:cen.eu:en16931:2017");
    }


    @Test
    public void simpleSimple() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/documents/uncefact-simple.xml");
        Assert.assertEquals(
                declarationDetector.detect(inputStream).getIdentifier().get(0),
                "urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100::CrossIndustryInvoice");
    }
}
