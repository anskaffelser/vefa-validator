package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Config;
import no.difi.vefa.validator.config.SimpleConfig;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * Class to hold defaults in validator.
 */
class ValidatorDefaults {

    /**
     * Default configuration.
     */
    final static Config config = new SimpleConfig()
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
