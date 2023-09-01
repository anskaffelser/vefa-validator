package no.difi.vefa.validator.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface XmlUtils {

    Pattern ROOT_TAG_PATTERN = Pattern.compile(
            "<(?!http[s]{0,1}://)(\\w*:{0,1}[^<?|^<!^]*?)>", Pattern.MULTILINE);

    Pattern NAMESPACE_PATTERN = Pattern.compile(
            "xmlns:{0,1}([A-Za-z0-9\\-]*)\\w*=\\w*[\"']{1}(.+?)[\"']{1}", Pattern.MULTILINE);

    Pattern COMMENTS_PATTERN =
            Pattern.compile("<!--(.+?)-->", Pattern.MULTILINE);

    static String extractRootNamespace(String xmlContent) {
        Matcher matcher = ROOT_TAG_PATTERN.matcher(removeComments(xmlContent));
        if (matcher.find()) {
            String rootElement = matcher.group(1).trim().replace("\n", " ").replace("\r", "").replace("\t", " ");
            // logger.debug("Root element: {}", rootElement);
            String rootNs = rootElement.split(" ", 2)[0].contains(":") ? rootElement.substring(0, rootElement.indexOf(":")) : "";
            // logger.debug("Root ns: {}", rootNs);

            Matcher nsMatcher = NAMESPACE_PATTERN.matcher(rootElement);
            while (nsMatcher.find()) {
                // logger.debug(nsMatcher.group(0));

                if (nsMatcher.group(1).equals(rootNs)) {
                    return nsMatcher.group(2);
                }
            }
        }

        return null;
    }

    static String extractLocalName(String xmlContent) {
        Matcher matcher = ROOT_TAG_PATTERN.matcher(removeComments(xmlContent));
        if (matcher.find()) {
            String rootElement = matcher.group(1).trim().replace("\n", " ").replace("\r", "").replace("\t", " ");
            // logger.debug("Root element: {}", rootElement);
            return rootElement.split(" ", 2)[0].contains(":") ?
                    rootElement.substring(rootElement.indexOf(":") + 1, rootElement.indexOf(" ")) :
                    rootElement.split(" ", 2)[0];
        }
        return null;
    }

    static String removeComments(String xmlContent) {
        return COMMENTS_PATTERN.matcher(xmlContent).replaceAll("");
    }
}
