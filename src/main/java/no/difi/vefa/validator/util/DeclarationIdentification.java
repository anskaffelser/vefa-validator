package no.difi.vefa.validator.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import no.difi.vefa.validator.api.Document;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.expectation.ValidatorTestExpectation;
import no.difi.vefa.validator.expectation.XmlExpectation;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.xsd.vefa.validator._1_0.internal.IdentificationType;
import no.difi.xsd.vefa.validator._1_0.internal.PropertyType;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeclarationIdentification {

    public static final DeclarationIdentification UNKNOWN = new DeclarationIdentification();

    private static final JAXBContext JAXB = JaxbUtils.context(IdentificationType.class);

    private final String type;

    private final List<String> identifiers;

    private final Map<String, String> properties;

    private final List<Object> children;

    private final Object converted;

    public static DeclarationIdentification of(ByteArrayOutputStream baos) throws ValidatorException {
        return of(new ByteArrayInputStream(baos.toByteArray()));
    }

    public static DeclarationIdentification of(InputStream inputStream) throws ValidatorException {
        try {
            return new DeclarationIdentification(JAXB.createUnmarshaller()
                    .unmarshal(new StreamSource(inputStream), IdentificationType.class).getValue());
        } catch (JAXBException e) {
            throw new ValidatorException("Unable to parse detector result.", e);
        }
    }

    private DeclarationIdentification() {
        this.type = null;
        this.identifiers = List.of("unknown");
        this.properties = Collections.emptyMap();
        this.children = null;
        this.converted = null;
    }

    private DeclarationIdentification(IdentificationType it) {
        this.type = it.getType();
        this.identifiers = it.getId();
        this.properties = it.getProperty() == null ? Collections.emptyMap() :
                it.getProperty().stream().collect(Collectors.toMap(PropertyType::getKey, PropertyType::getValue));
        this.children = it.getChildren() == null ? null : it.getChildren().getAny();
        this.converted = it.getConverted() == null ? null : it.getConverted().getAny();
    }

    public String getType() {
        return type;
    }

    public List<String> getIdentifier() {
        return identifiers;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public List<String> getFullIdentifier() {
        if (type == null)
            return Collections.emptyList();

        return identifiers.stream()
                .map(id -> id.startsWith("configuration::") ? id : String.format("%s::%s", type, id))
                .toList();
    }

    public boolean hasChildren() {
        return children != null;
    }

    public List<Document> getChildren() throws ValidatorException {
        var result = new ArrayList<Document>();

        for (Object child : children) {
            if (child instanceof Node c)
                result.add(Document.of(c));
            else
                throw new ValidatorException("Unable to convert XML.");
        }

        return result;
    }

    public boolean hasConverted() {
        return converted != null;
    }

    public Document getConverted() throws ValidatorException {
        if (converted instanceof Node c)
            return Document.of(c);

        throw new ValidatorException("No converted document found.");
    }

    public Expectation expectations(Document document) {
        return "xml.test".equals(type) ? new ValidatorTestExpectation(document) : new XmlExpectation(document);
    }

    @Override
    public String toString() {
        String identifier = identifiers.get(0);

        if (identifier.startsWith("configuration::"))
            return identifier;

        return type == null ? "NA" : String.format("%s::%s", type, identifier);
    }
}
