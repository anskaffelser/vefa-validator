package no.difi.vefa.validator.util;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * @author erlend
 */
public class ClasspathURIResolver implements URIResolver {

    private String path;

    public ClasspathURIResolver(String path) {
        this.path = path;
    }

    public Source resolve(String href, String base) {
        return !"".equals(base) ? null : new StreamSource(
                this.getClass().getResourceAsStream(String.format("%s/%s", this.path, href)));
    }
}