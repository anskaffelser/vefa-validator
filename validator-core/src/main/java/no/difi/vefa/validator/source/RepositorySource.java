package no.difi.vefa.validator.source;

import no.difi.vefa.validator.ValidatorException;
import no.difi.vefa.validator.api.SourceInstance;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Defines a repository as source for validation artifacts.
 */
public class RepositorySource extends AbstractSource {

    public static RepositorySource forTest() {
        return create("https://test-vefa.difi.no/validator/repo/");
    }

    public static RepositorySource forProduction() {
        return create("http://vefa.difi.no/validator/repo/");
    }

    static RepositorySource create(String uri) {
        try {
            return new RepositorySource(uri);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private URI rootUri;

    public RepositorySource(String uri) throws URISyntaxException {
        this(new URI(uri));
    }

    /**
     * Initiate the new source.
     */
    public RepositorySource(URI uri) {
        this.rootUri = uri;
    }

    @Override
    public SourceInstance createInstance() throws ValidatorException{
        return new RepositorySourceInstance(rootUri);
    }
}
