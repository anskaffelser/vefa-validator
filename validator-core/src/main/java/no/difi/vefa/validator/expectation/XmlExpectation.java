package no.difi.vefa.validator.expectation;

import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.Section;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class XmlExpectation implements Expectation {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(XmlExpectation.class);

    private String description;
    private Map<String, Integer> successes = new HashMap<>();
    private Map<String, Integer> warnings = new HashMap<>();
    private Map<String, Integer> errors = new HashMap<>();
    private Map<String, Integer> fatals = new HashMap<>();

    public XmlExpectation(byte[] bytes) {
        String content = new String(bytes);
        if (!content.contains("<!--") || !content.contains("-->"))
            return;

        content = content.substring(content.indexOf("<!--") + 4, content.indexOf("-->"));

        for (String section : content.replaceAll("\\r", "").replaceAll("\\t", " ").replaceAll("  ", "")/*.replaceAll(" \\n", "\\n").replaceAll("\\n ", "\\n")*/.trim().split("\\n\\n")) {
            String[] parts = section.split(":", 2);
            switch (parts[0].toLowerCase()) {
                case "content":
                case "description":
                    description = parts[1].trim().replaceAll("\\n", " ").replaceAll("  ", " ");
                    break;

                case "success":
                case "successes":
                    extractRules(parts, successes);
                    break;

                case "warning":
                case "warnings":
                    extractRules(parts, warnings);
                    break;

                case "error":
                case "errors":
                    extractRules(parts, errors);
                    break;

                case "fatal":
                case "fatals":
                    extractRules(parts, fatals);
                    break;
            }
        }
    }

    private void extractRules(String[] parts, Map<String, Integer> target) {
        for (String p : parts[1].replaceAll(" x ", " ").replaceAll(" * ", " ").replaceAll("None", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("times", "").replaceAll("time", "").replaceAll("  ", "").trim().split("\\n")) {
            try {
                if (!p.trim().isEmpty()) {
                    String[] r = p.trim().split(" ");
                    if (!target.containsKey(r[0]))
                        target.put(r[0], 0);
                    target.put(r[0], target.get(r[0]) + (r.length == 1 ? 1 : Integer.parseInt(r[1])));
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void filterFlag(AssertionType assertionType) {
        if (assertionType.getFlag() != null) {
            if (successes.containsKey(assertionType.getIdentifier())) {
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
                section.add("SYSTEM-004", "Rule '" + key + "' (FATAL) not fired " + fatals.get(key) + " time(s).", FlagType.ERROR);
        for (String key : errors.keySet())
            if (errors.get(key) > 0)
                section.add("SYSTEM-005", "Rule '" + key + "' (ERROR) not fired " + errors.get(key) + " time(s).", FlagType.ERROR);
        for (String key : warnings.keySet())
            if (warnings.get(key) > 0)
                section.add("SYSTEM-006", "Rule '" + key + "' (WARNING) not fired " + warnings.get(key) + " time(s).", FlagType.ERROR);
        for (String key : successes.keySet()) {
            if (successes.get(key) == 1)
                section.add(key, "Rule not fired.", FlagType.SUCCESS);
            else
                section.add("SYSTEM-009", "Rule '" + key + "' fired " + (successes.get(key) - 1) + " time(s).", FlagType.ERROR);
        }
    }
}
