package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.StreamUtils;
import no.difi.vefa.validator.util.XmlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Type("xml")
public class XmlDeclaration extends AbstractXmlDeclaration {

    @Override
    public boolean verify(byte[] content, List<String> parent) throws ValidatorException {
        return XmlUtils.extractRootNamespace(new String(content)) != null;
    }

    @Override
    public List<String> detect(InputStream contentStream, List<String> parent) throws ValidatorException {

        try {
            byte[] content = StreamUtils.readAndReset(contentStream, 10 * 1024);
            String c = new String(content);
            return Collections.singletonList(String.format(
                    "%s::%s", XmlUtils.extractRootNamespace(c), XmlUtils.extractLocalName(c)));
        } catch (IOException e) {
            // Simply ignore
        }

        return null;
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }
}
