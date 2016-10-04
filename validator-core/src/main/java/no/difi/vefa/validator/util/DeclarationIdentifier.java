package no.difi.vefa.validator.util;

public class DeclarationIdentifier {

    private DeclarationIdentifier parent;
    private DeclarationWrapper declaration;
    private String identifier;

    public DeclarationIdentifier(DeclarationIdentifier parent, DeclarationWrapper declaration, String identifier) {
        this.parent = parent;
        this.declaration = declaration;
        this.identifier = identifier;
    }

    public DeclarationIdentifier getParent() {
        return parent;
    }

    public DeclarationWrapper getDeclaration() {
        return declaration;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        if (identifier.startsWith("configuration::"))
            return identifier;
        return declaration == null ? "NA" : String.format("%s::%s", declaration.getType(), identifier);
    }
}
