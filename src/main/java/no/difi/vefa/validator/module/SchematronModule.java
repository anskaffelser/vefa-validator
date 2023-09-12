package no.difi.vefa.validator.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.XsltExecutable;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.XsltHelper;

/**
 * @author erlend
 */
@Slf4j
public class SchematronModule extends AbstractModule {

    @Provides
    @Named("schematron-step3")
    @Singleton
    public XsltExecutable getSchematronCompiler(XsltHelper helper) throws ValidatorException {
        return helper.fromResource("/iso-schematron-xslt2/iso_svrl_for_xslt2.xsl");
    }

    @Provides
    @Named("schematron-svrl-parser")
    @Singleton
    public XsltExecutable getSchematronSvrlParser(XsltHelper helper) throws ValidatorException {
        return helper.fromResource("/vefa-validator/xslt/svrl-parser.xslt");
    }
}
