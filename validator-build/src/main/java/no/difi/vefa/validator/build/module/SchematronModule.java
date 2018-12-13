package no.difi.vefa.validator.build.module;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.sf.saxon.s9api.Processor;
import no.difi.commons.schematron.SchematronCompiler;
import no.difi.commons.schematron.SchematronException;
import org.kohsuke.MetaInfServices;

/**
 * @author erlend
 */
@MetaInfServices(Module.class)
public class SchematronModule extends AbstractModule {

    @Provides
    @Singleton
    public SchematronCompiler getSchematronCompiler(Processor processor) {
        try {
            return new SchematronCompiler(processor);
        } catch (SchematronException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
