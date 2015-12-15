package no.difi.vefa.validator.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ConvertedDocument extends Document {
    private ByteArrayInputStream source;

    public ConvertedDocument(ByteArrayInputStream inputStream, ByteArrayInputStream source, String declaration, Expectation expectation) throws IOException, ValidatorException {
        super(inputStream, declaration, expectation);
        this.source = source;
    }

    public ByteArrayInputStream getSource() {
        return source;
    }
}
