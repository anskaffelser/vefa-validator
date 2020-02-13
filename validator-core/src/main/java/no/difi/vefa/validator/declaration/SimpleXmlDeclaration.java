package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.StreamUtils;
import no.difi.vefa.validator.util.XmlUtils;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleXmlDeclaration extends AbstractXmlDeclaration {

    protected String namespace;

    protected String localName;

    public SimpleXmlDeclaration(String namespace, String localName) {
        this.namespace = namespace;
        this.localName = localName;
    }

    @Override
    public boolean verify(byte[] content, List<String> parent) throws ValidatorException {
        String c = new String(content);
        return namespace.equals(XmlUtils.extractRootNamespace(c)) &&
                (localName == null || localName.equals(XmlUtils.extractLocalName(c)));
    }

    @Override
    public List<String> detect(InputStream contentStream, List<String> parent) throws ValidatorException {

        try {
            byte[] bytes = StreamUtils.readAndReset(contentStream, 10*1024);
            return Collections.singletonList(String.format(
                    "%s::%s", namespace, localName == null ?
                            XmlUtils.extractLocalName(new String(bytes)) :
                            localName));
        }catch (IOException e){
            new ValidationException("Couldn't detect SimpleXmlDeclaration", e);
        }

        return null;

    }
}
