package no.difi.vefa.validator.util;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

public class BlockingURIResolver implements URIResolver {

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        if (href.contains(":/"))
            throw new TransformerException(String.format("Blocking request to '%s'.", href));

        return null;
    }
}
