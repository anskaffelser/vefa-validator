package no.difi.vefa.validator.util;

import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;
import no.difi.vefa.validator.lang.ValidatorException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeclarationWrapper implements Declaration, DeclarationWithChildren, DeclarationWithConverter {

    private String type;

    private Declaration declaration;

    private List<DeclarationWrapper> children = new ArrayList<>();

    public static DeclarationWrapper of(String type, Declaration declaration) {
        return new DeclarationWrapper(type, declaration);
    }

    private DeclarationWrapper(String type, Declaration declaration) {
        this.type = type;
        this.declaration = declaration;
    }

    public String getType() {
        return type;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public List<DeclarationWrapper> getChildren() {
        return children;
    }

    @Override
    public boolean verify(byte[] content, List<String> parent) throws ValidatorException {
        return declaration.verify(content, parent);
    }

    @Override
    public List<String> detect( InputStream contentStream, List<String> parent) throws ValidatorException {
        return declaration.detect(contentStream, parent);
    }

    @Override
    public Expectation expectations(byte[] content) throws ValidatorException {
        return declaration.expectations(content);
    }

    public boolean supportsChildren() {
        return declaration instanceof DeclarationWithChildren;
    }

    @Override
    public Iterable<CachedFile> children(InputStream inputStream) throws ValidatorException {
        return ((DeclarationWithChildren) declaration).children(inputStream);
    }

    public boolean supportsConverter() {
        return declaration instanceof DeclarationWithConverter;
    }

    @Override
    public void convert(InputStream inputStream, OutputStream outputStream) throws ValidatorException {
        ((DeclarationWithConverter) declaration).convert(inputStream, outputStream);
    }

    @Override
    public String toString() {
        return type + " // " + declaration.getClass().getName();
    }
}
