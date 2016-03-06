package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.util.XmlUtils;

public class SbdhDeclaration implements Declaration {

    @Override
    public boolean verify(byte[] content) throws ValidatorException {
        String namespace = XmlUtils.extractRootNamespace(new String(content));
        return "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader".equals(namespace);
    }

    @Override
    public String detect(byte[] content) throws ValidatorException {
        // Simple stupid
        return "SBDH:1.0";
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
