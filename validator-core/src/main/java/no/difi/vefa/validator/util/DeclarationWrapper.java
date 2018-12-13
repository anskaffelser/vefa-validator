package no.difi.vefa.validator.util;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeclarationWrapper implements Declaration, DeclarationWithChildren, DeclarationWithConverter {

    private Declaration declaration;
    private String type;
    private List<DeclarationWrapper> children = new ArrayList<>();

    public DeclarationWrapper(Config config) {
        try {
            type = config.getString("type");

            Class<? extends Declaration> cls = (Class<? extends Declaration>) Class.forName(config.getString("class"));

            try {
                declaration = cls.getConstructor(Config.class).newInstance();
            } catch (Exception e) {
                try {
                    declaration = cls.getConstructor().newInstance();
                } catch (Exception ex) {
                    log.warn(e.getMessage(), ex);
                }
            }
        } catch (ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        }
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
    public boolean verify(byte[] content, String parent) throws ValidatorException {
        return declaration.verify(content, parent);
    }

    @Override
    public String detect(byte[] content, String parent) throws ValidatorException {
        return declaration.detect(content, parent);
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
