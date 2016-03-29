package no.difi.vefa.validator.api;

import java.util.Iterator;

/**
 * Extending Iterator to also provide the index of witch is currently provided.
 *
 * @param <T> Type of index.
 */
public interface IndexedIterator<T> extends Iterator<T> {

    /**
     * @return Current index.
     */
    String currentIndex();

}
