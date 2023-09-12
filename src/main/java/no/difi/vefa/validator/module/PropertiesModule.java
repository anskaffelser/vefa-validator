package no.difi.vefa.validator.module;

import com.google.inject.Module;
import no.difi.vefa.validator.model.Prop;
import no.difi.vefa.validator.model.Props;

/**
 * @author erlend
 */
public interface PropertiesModule {

    static Module with(Prop... props) {
        return binder -> binder.bind(Props.class).toInstance(Props.init().update(props));
    }
}
