package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.util.XmlUtils;
import org.kohsuke.MetaInfServices;

@MetaInfServices(Declaration.class)
public class XmlDeclaration extends AbstractXmlDeclaration {

    @Override
    public boolean verify(byte[] content, String parent) throws ValidatorException {
        return XmlUtils.extractRootNamespace(new String(content)) != null;
    }

    @Override
    public String detect(byte[] content, String parent) throws ValidatorException {
        String c = new String(content);
        return String.format("%s::%s", XmlUtils.extractRootNamespace(c), XmlUtils.extractLocalName(c));
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }
}
