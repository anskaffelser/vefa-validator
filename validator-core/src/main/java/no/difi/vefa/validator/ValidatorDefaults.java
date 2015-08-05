package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Config;
import no.difi.vefa.validator.config.SimpleConfig;

class ValidatorDefaults {

    final static Config config = new SimpleConfig()
            // pools.checker
            .set("pools.checker.blockerWhenExhausted", false)

            // pools.presenter
            .set("pools.presenter.blockerWhenExhausted", false)

            // finish
            ;

}
