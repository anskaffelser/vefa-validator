package no.dfo.anskaffelser.vefa.validator.declaration;

import no.dfo.anskaffelser.vefa.validator.api.Declaration;
import no.dfo.anskaffelser.vefa.validator.api.Expectation;
import no.dfo.anskaffelser.vefa.validator.expectation.XmlExpectation;
import no.dfo.anskaffelser.vefa.validator.lang.ValidatorException;

import javax.xml.stream.XMLInputFactory;

abstract class AbstractXmlDeclaration implements Declaration {

    protected static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newFactory();

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}
