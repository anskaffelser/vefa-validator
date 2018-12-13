package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

abstract class AbstractXmlDeclaration implements Declaration {

    protected static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    protected static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
