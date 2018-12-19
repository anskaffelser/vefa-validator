package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.XmlUtils;

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
    public List<String> detect(byte[] content, List<String> parent) throws ValidatorException {
        return Collections.singletonList(String.format(
                "%s::%s", namespace, localName == null ?
                        XmlUtils.extractLocalName(new String(content)) :
                        localName));
    }
}
