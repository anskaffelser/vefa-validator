package no.difi.vefa.validator.expectation;

import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.Section;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractExpectation implements Expectation {

    protected String description;

    protected List<String> scopes = new ArrayList<>();

    protected Map<String, Integer> successes = new HashMap<>();

    protected Map<String, Integer> warnings = new HashMap<>();

    protected Map<String, Integer> errors = new HashMap<>();

    protected Map<String, Integer> fatals = new HashMap<>();

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void filterFlag(AssertionType assertionType) {
        if (assertionType.getFlag() == null)
            return;

        if (!scopes.isEmpty() && !scopes.contains(assertionType.getIdentifier())) {
            assertionType.setFlag(null);
        } else if (successes.containsKey(assertionType.getIdentifier())) {
            assertionType.setFlag(FlagType.ERROR);
            successes.put(assertionType.getIdentifier(), successes.get(assertionType.getIdentifier()) + 1);
        } else {
            switch (assertionType.getFlag()) {
                case FATAL:
                    if (isExpected(assertionType.getIdentifier(), fatals))
                        assertionType.setFlag(FlagType.EXPECTED);
                    break;
                case ERROR:
                    if (isExpected(assertionType.getIdentifier(), errors))
                        assertionType.setFlag(FlagType.EXPECTED);
                    break;
                case WARNING:
                    if (isExpected(assertionType.getIdentifier(), warnings))
                        assertionType.setFlag(FlagType.EXPECTED);
                    break;
            }
        }
    }

    private boolean isExpected(String identifier, Map<String, Integer> target) {
        if (!target.containsKey(identifier) || target.get(identifier) == 0)
            return false;
        target.put(identifier, target.get(identifier) - 1);
        return true;
    }

    @Override
    public void verify(Section section) {
        for (String key : fatals.keySet())
            if (fatals.get(key) > 0)
                section.add("SYSTEM-004", String.format(
                        "Rule '%s' (FATAL) not fired %s time(s).", key, fatals.get(key)), FlagType.ERROR);
        for (String key : errors.keySet())
            if (errors.get(key) > 0)
                section.add("SYSTEM-005", String.format(
                        "Rule '%s' (ERROR) not fired %s time(s).", key, errors.get(key)), FlagType.ERROR);
        for (String key : warnings.keySet())
            if (warnings.get(key) > 0)
                section.add("SYSTEM-006", String.format(
                        "Rule '%s' (WARNING) not fired %s time(s).", key, warnings.get(key)), FlagType.ERROR);
        for (String key : successes.keySet()) {
            if (successes.get(key) == 1)
                section.add(key, "Rule not fired.", FlagType.SUCCESS);
            else
                section.add("SYSTEM-009", String.format(
                        "Rule '%s' fired %s time(s).", key, successes.get(key) - 1), FlagType.ERROR);
        }
    }
}
