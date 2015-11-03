package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;

public class EdifactDeclaration implements Declaration {

    @Override
    public boolean verify(String content) throws ValidatorException {
        return content.contains("UNH+");
    }

    @Override
    public String detect(String content) throws ValidatorException {
        return null;
    }

    @Override
    public Expectation expectations(String content) throws ValidatorException {
        return null;
    }
}
