package no.difi.vefa.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.model.Document;
import no.difi.vefa.validator.model.Prop;
import no.difi.xsd.vefa.validator._1.PackageType;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Validator containing an instance of validation configuration and validation artifacts.
 * <p/>
 * Validator is thread safe and should normally be created only once in a program.
 */
@Slf4j
@Singleton
public class Validator implements Closeable {

    /**
     * Current validator instance.
     */
    @Inject
    private ValidatorInstance validatorInstance;

    /**
     * Validate file.
     *
     * @param file  File to validate.
     * @param props Optional properties.
     * @return Validation result.
     */
    public Validation validate(File file, Prop... props) throws IOException {
        return validate(Document.of(file), props);
    }

    /**
     * Validate file.
     *
     * @param file  File to validate.
     * @param props Optional properties.
     * @return Validation result.
     */
    public Validation validate(Path file, Prop... props) throws IOException {
        return validate(Document.of(file), props);
    }

    /**
     * Validate content of stream.
     *
     * @param inputStream Stream containing content.
     * @param props       Optional properties.
     * @return Validation result.
     */
    public Validation validate(InputStream inputStream, Prop... props) throws IOException {
        return validate(Document.of(inputStream), props);
    }

    /**
     * Validate document.
     *
     * @param document File to validate.
     * @param props    Optional properties.
     * @return Validation result.
     */
    public Validation validate(Document document, Prop... props) {
        return validatorInstance.validate(document, props);
    }

    /**
     * List of packages supported by validator.
     *
     * @return List of packages.
     */
    public List<PackageType> getPackages() {
        return validatorInstance.getPackages();
    }

    @Override
    public void close() {
        try {
            if (validatorInstance != null)
                validatorInstance.close();
        } catch (IOException e) {
            log.warn("Exception when closing Validator: {}", e.getMessage(), e);
        } finally {
            validatorInstance = null;
        }
    }
}
