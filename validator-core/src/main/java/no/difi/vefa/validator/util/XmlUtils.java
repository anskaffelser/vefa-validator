package no.difi.vefa.validator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlUtils {

    private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    private static final Pattern rootTagPattern = Pattern.compile("<(?!http[s]{0,1}://)(\\w*:{0,1}[^<?|^<!^]*?)>", Pattern.MULTILINE);
    private static final Pattern namespacePattern = Pattern.compile("xmlns:{0,1}([A-Za-z0-9]*)\\w*=\\w*[\"']{1}(.+?)[\"']{1}", Pattern.MULTILINE);
    private static final Pattern localNamePattern = Pattern.compile("([A-Za-z0-9]*:)([A-Za-z0-9\\-]+).*", Pattern.MULTILINE);

    public static String extractRootNamespace(String xmlContent) {
        Matcher matcher = rootTagPattern.matcher(xmlContent);
        if (matcher.find()) {
            String rootElement = matcher.group(1).trim();
            logger.debug("Root element: {}", rootElement);
            String rootNs = rootElement.split(" ", 2)[0].contains(":") ? rootElement.substring(0, rootElement.indexOf(":")) : "";
            logger.debug("Namespace: {}", rootNs);

            Matcher nsMatcher = namespacePattern.matcher(rootElement);
            while (nsMatcher.find()) {
                logger.debug(nsMatcher.group(0));

                if (nsMatcher.group(1).equals(rootNs)) {
                    return nsMatcher.group(2);
                }
            }
        }

        return null;
    }

    public static String extractLocalName(String xmlContent) {
        Matcher matcher = rootTagPattern.matcher(xmlContent);
        if (matcher.find()) {
            Matcher lnMatcher = localNamePattern.matcher(matcher.group(1).trim());
            if (lnMatcher.find())
                return lnMatcher.group(2);
        }
        return null;
    }

    XmlUtils() {

    }
}
