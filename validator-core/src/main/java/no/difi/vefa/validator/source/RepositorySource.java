package no.difi.vefa.validator.source;

import no.difi.vefa.validator.ValidatorException;
import no.difi.vefa.validator.api.SourceInstance;

import java.net.URI;

/**
 * Defines a repository as source for validation artifacts.
 */
public class RepositorySource extends AbstractSource {

    public static RepositorySource forTest() {
        try {
            return new RepositorySource(new URI("http://test.vefa.difi.no/validator/repo/"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static RepositorySource forProduction() {
        try {
            return new RepositorySource(new URI("http://vefa.difi.no/validator/repo/"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private URI rootUri;

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
