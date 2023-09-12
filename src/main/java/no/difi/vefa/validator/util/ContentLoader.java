package no.difi.vefa.validator.util;

import com.google.inject.Singleton;
import net.sf.saxon.lib.ResourceRequest;
import net.sf.saxon.lib.ResourceResolver;
import net.sf.saxon.trans.XPathException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class ContentLoader implements ResourceResolver {

    @Override
    public Source resolve(ResourceRequest request) throws XPathException {
        return resolve(request.uri);
    }

    public Source resolve(String uri) throws XPathException {
        try {
            // Loading resource
            return new StreamSource(open(uri), uri);
        } catch (IOException e) {
            // Make sure to not hand over loading to default resolver
            throw new XPathException(String.format("Unable to load resource: %s", uri));
        }
    }

    public InputStream open(String uri) throws IOException {
        // Loading resource from jar files
        if (uri.startsWith("res:")) {
            var inputStream = getClass().getResourceAsStream(uri.substring(4));
            if (inputStream == null)
                throw new IOException(String.format("Resource not found: %s", uri));

            return inputStream;
        }

        throw new IOException(String.format("Unable to load resource: %s", uri));
    }
}
