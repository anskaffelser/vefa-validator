package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.properties.SimpleProperties;

/**
 * Class to hold defaults in validator.
 */
public class ValidatorDefaults {

    /**
     * Default configuration.
     */
    public static final Properties PROPERTIES = new SimpleProperties()

            // feature
            .set("feature.expectation", false)
            .set("feature.nesting", false)
            .set("feature.suppress_notloaded", false)
            .set("feature.infourl", false)

            // pools.checker
            .set("pools.checker.size", 250)
            .set("pools.checker.expire", 60 * 24)

            // pools.presenter
            .set("pools.presenter.size", 250)
            .set("pools.presenter.expire", 60 * 24)

            // finish
            ;

}
