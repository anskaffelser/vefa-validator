package no.difi.vefa.validator.api;

import java.util.Iterator;

public interface IndexedIterator<T> extends Iterator<T> {

    String currentIndex();

}
