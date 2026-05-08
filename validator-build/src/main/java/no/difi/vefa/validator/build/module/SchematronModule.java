package no.difi.vefa.validator.build.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.sf.saxon.s9api.Processor;
import no.difi.commons.schematron.SchematronCompiler;
import no.difi.commons.schematron.SchematronException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author erlend
 */
public class SchematronModule extends AbstractModule {

    @Provides
    @Named("compile")
    @Singleton
    public SchematronCompiler getSchematronCompiler(Processor processor) {
        try {
            return new SchematronCompiler(processor);
        } catch (SchematronException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Provides
    @Named("prepare")
    @Singleton
    public SchematronCompiler getSchematronPreparer(Processor processor) {
        try {
            SchematronCompiler schematronCompiler = new SchematronCompiler(processor);
            Field field = schematronCompiler.getClass().getDeclaredField("steps");
            field.setAccessible(true);

            List originalList = (List) field.get(schematronCompiler);

            List<Object> newList = new ArrayList<>();
            newList.add(originalList.get(0));
            newList.add(originalList.get(1));

            field.set(schematronCompiler, newList);

            return schematronCompiler;
        } catch (SchematronException | NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
