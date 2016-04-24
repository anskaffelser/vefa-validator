package no.difi.vefa.validator.expectation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class XmlExpectation extends AbstractExpectation {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(XmlExpectation.class);

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

                case "scope":
                    extractList(parts, scopes);
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

    private void extractList(String[] parts, List<String> target) {
        for (String part : parts[1].split("\\n"))
            if (!part.trim().isEmpty())
                target.add(part.trim());
    }
}
