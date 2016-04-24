package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.util.XmlUtils;

public class SimpleXmlDeclaration extends XmlDeclaration {

    protected String namespace;
    protected String localName;

    public SimpleXmlDeclaration(String namespace, String localName) {
        this.namespace = namespace;
        this.localName = localName;
    }

    @Override
    public boolean verify(byte[] content) throws ValidatorException {
        String c = new String(content);
        return namespace.equals(XmlUtils.extractRootNamespace(c)) &&
                (localName == null || localName.equals(XmlUtils.extractLocalName(c)));
    }

    @Override
    public String detect(byte[] content) throws ValidatorException {
        return String.format("%s::%s", namespace, localName == null ? XmlUtils.extractLocalName(new String(content)) : localName);
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
