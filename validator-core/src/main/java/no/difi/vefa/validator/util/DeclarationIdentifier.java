package no.difi.vefa.validator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeclarationIdentifier {

    private DeclarationIdentifier parent;

    private DeclarationWrapper declaration;

    private List<String> identifiers;

    public DeclarationIdentifier(DeclarationIdentifier parent, DeclarationWrapper declaration,
                                 List<String> identifiers) {
        this.parent = parent;
        this.declaration = declaration;
        this.identifiers = identifiers;
    }

    public DeclarationIdentifier getParent() {
        return parent;
    }

    public DeclarationWrapper getDeclaration() {
        return declaration;
    }

    public List<String> getIdentifier() {
        return identifiers;
    }

    public List<String> getFullIdentifier() {
        if (declaration == null)
            return Collections.emptyList();

        List<String> result = new ArrayList<>();

        for (String identifier : identifiers) {
            if (identifier.startsWith("configuration::"))
                result.add(identifier);
            else
                result.add(String.format("%s::%s", declaration.getType(), identifier));
        }

        return result;
    }

    @Override
    public String toString() {
        String identifier = identifiers.get(0);

        if (identifier.startsWith("configuration::"))
            return identifier;
        return declaration == null ? "NA" : String.format("%s::%s", declaration.getType(), identifier);
    }
}
