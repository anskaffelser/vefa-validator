package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.XmlUtils;
import org.kohsuke.MetaInfServices;

import java.util.Collections;
import java.util.List;

@Type("xml")
@MetaInfServices(Declaration.class)
public class XmlDeclaration extends AbstractXmlDeclaration {

    @Override
    public boolean verify(byte[] content, List<String> parent) throws ValidatorException {
        return XmlUtils.extractRootNamespace(new String(content)) != null;
    }

    @Override
    public List<String> detect(byte[] content, List<String> parent) throws ValidatorException {
        String c = new String(content);
        return Collections.singletonList(String.format(
                "%s::%s", XmlUtils.extractRootNamespace(c), XmlUtils.extractLocalName(c)));
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return null;
    }
}
