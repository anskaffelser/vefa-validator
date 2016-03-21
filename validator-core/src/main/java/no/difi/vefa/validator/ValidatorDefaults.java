package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.properties.SimpleProperties;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * Class to hold defaults in validator.
 */
class ValidatorDefaults {

    /**
     * Default configuration.
     */
    final static Properties PROPERTIES = new SimpleProperties()

        // feature
        .set("feature.expectation", false)
        .set("feature.nesting", false)
        .set("feature.suppress_notloaded", false)

        // pools.checker
        .set("pools.checker.blockerWhenExhausted", GenericKeyedObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED)
        .set("pools.checker.lifo", GenericKeyedObjectPoolConfig.DEFAULT_LIFO)
        .set("pools.checker.maxTotal", GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL)
        .set("pools.checker.maxTotalPerKey", GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL_PER_KEY)

        // pools.presenter
        .set("pools.presenter.blockerWhenExhausted", GenericKeyedObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED)
        .set("pools.presenter.lifo", GenericKeyedObjectPoolConfig.DEFAULT_LIFO)
        .set("pools.presenter.maxTotal", GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL)
        .set("pools.presenter.maxTotalPerKey", GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL_PER_KEY)

        // finish
        ;

}
