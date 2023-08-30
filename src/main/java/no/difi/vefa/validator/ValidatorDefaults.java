package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.properties.SimpleProperties;

import java.util.concurrent.TimeUnit;

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
            .set("pools.checker.size", CheckerCacheLoader.DEFAULT_SIZE)
            .set("pools.checker.expire", TimeUnit.DAYS.toMinutes(1))

            // finish
            ;

}
