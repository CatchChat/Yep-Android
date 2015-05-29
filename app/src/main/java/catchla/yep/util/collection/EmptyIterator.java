package catchla.yep.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator<T> implements Iterator<T> {

    public boolean hasNext() {
        return false;
    }

    public T next() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}