package no.difi.vefa.validator.source;

import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.SourceInstance;
import no.difi.vefa.validator.api.ValidatorException;

import java.net.URI;

/**
 * Defines a repository as source for validation artifacts.
 */
public class RepositorySource extends AbstractSource {

    public static RepositorySource forTest() {
        return create("https://test-vefa.difi.no/validator/repo/");
    }

    public static RepositorySource forProduction() {
        return create("https://vefa.difi.no/validator/repo/");
    }

    static RepositorySource create(String uri) {
        return new RepositorySource(uri);
    }

    private URI rootUri;

    /**
     * Helper method to allow using string when initiating the new source.
     *
     * @param uri Uri used to fetch validation artifacts.
     */
    public RepositorySource(String uri) {
        this(URI.create(uri));
    }

    /**
     * Initiate the new source.
     *
     * @param uri Uri used to fetch validation artifacts.
     */
    public RepositorySource(URI uri) {
        this.rootUri = uri;
    }

    @Override
    public SourceInstance createInstance(Properties properties) throws ValidatorException {
        return new RepositorySourceInstance(properties, rootUri);
    }
}
