package no.difi.vefa.validator.util;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import no.difi.vefa.validator.lang.ValidatorException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class XsltHelper {

    @Inject
    private Provider<XsltCompiler> compilerProvider;

    public XsltExecutable fromResource(String path) throws ValidatorException {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            return compilerProvider.get().compile(new StreamSource(inputStream, "res:" + path));
        } catch (IOException | SaxonApiException e) {
            throw new ValidatorException("Unable to load XSLT from resource.");
        }
    }
}
