package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.util.XmlUtils;

public class SbdhDeclaration implements Declaration {

    @Override
    public boolean verify(String content) throws ValidatorException {
        String namespace = XmlUtils.extractRootNamespace(content);
        return "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader".equals(namespace);
    }

    @Override
    public String detect(String content) throws ValidatorException {
        // Simple stupid
        return "SBDH:1.0";
    }

    @Override
    public Expectation expectations(String content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
