package novoda.android.typewriter.cursor;

import android.database.Cursor;

import java.io.Closeable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CursorList<T> implements List<T>, Closeable {

    private final Cursor cursor;
    private final CursorMarshaller<T> marshaller;

    public CursorList(Cursor cursor, Class<T> type) {
        this(cursor, new ReflectionCursorMarshaller<T>(cursor, type));
    }

    public CursorList(Cursor cursor, CursorMarshaller<T> marshaller) {
        this.cursor = cursor;
        this.marshaller = marshaller;
    }

    @Override
    public void close() {
        cursor.close();
    }

    @Override
    public int size() {
        if (cursor.isClosed()) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        if (cursor.isClosed()) {
            return new EmptyListIterator<T>();
        }
        return new CursorListIterator<T>(cursor, marshaller, i);
    }

    @Override
    public T get(int index) {
        if (cursor.moveToPosition(index)) {
            return marshaller.marshall(cursor);
        } else {
            throw new CursorListException("CursorList tries to access data at index " + index + " while cursor has size " + cursor.getCount());
        }
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        throw new UnsupportedOperationException();
    }

    public static class CursorListException extends RuntimeException {
        public CursorListException(String message) {
            super(message);
        }
    }
}
