package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Trigger;
import no.difi.vefa.validator.api.TriggerInfo;
import no.difi.vefa.validator.api.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class TriggerFactory {

    private static Logger logger = LoggerFactory.getLogger(TriggerFactory.class);

    private Map<String, Trigger> triggers = new HashMap<>();

    @SafeVarargs
    public TriggerFactory(Class<? extends Trigger>... triggerImpls) {
        for (Class<? extends Trigger> trigger : triggerImpls) {
            try {
                triggers.put(trigger.getAnnotation(TriggerInfo.class).value(), trigger.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                logger.info("Unable to load '{}'", trigger, e);
            }
        }
    }

    public Trigger get(String identifier) throws ValidatorException {
        if (triggers.containsKey(identifier))
            return triggers.get(identifier);

        throw new ValidatorException(String.format("Trigger '%s' not found.", identifier));
    }
}
