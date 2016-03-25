package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

abstract class XmlDeclaration implements Declaration {

    protected static XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    protected static XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();

}
