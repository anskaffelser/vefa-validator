package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.sf.saxon.s9api.XsltExecutable;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.XsltHelper;

public class TestingModule extends AbstractModule {

    @Provides
    @Singleton
    @Named("test-preparer")
    public XsltExecutable getDetectorXslt(XsltHelper helper) throws ValidatorException {
        return helper.fromResource("/vefa-validator/xslt/test-preparer.xslt");
    }
}
