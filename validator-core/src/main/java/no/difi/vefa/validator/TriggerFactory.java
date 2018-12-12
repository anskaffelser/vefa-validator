package no.difi.vefa.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Trigger;
import no.difi.vefa.validator.api.TriggerInfo;
import no.difi.vefa.validator.api.ValidatorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Singleton
class TriggerFactory {

    private Map<String, Trigger> triggers = new HashMap<>();

    @Inject
    public TriggerFactory(Set<Class<? extends Trigger>> triggerImpls) {
        for (Class<? extends Trigger> trigger : triggerImpls) {
            try {
                triggers.put(trigger.getAnnotation(TriggerInfo.class).value(), trigger.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                log.info("Unable to load '{}'", trigger, e);
            }
        }
    }

    public Trigger get(String identifier) throws ValidatorException {
        if (triggers.containsKey(identifier))
            return triggers.get(identifier);

        throw new ValidatorException(String.format("Trigger '%s' not found.", identifier));
    }
}
