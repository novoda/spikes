package novoda.android.typewriter.cursor;

import android.database.Cursor;

import java.util.ListIterator;

public class CursorListIterator<T> implements ListIterator<T> {

    private final Cursor cursor;
    private final CursorMarshaller<T> marshaller;
    private int index;

    public CursorListIterator(Cursor cursor, CursorMarshaller<T> marshaller, int index) {
        this.cursor = cursor;
        this.marshaller = marshaller;
        this.index = index;
    }

    @Override
    public boolean hasNext() {
        return index < cursor.getCount();
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public T next() {
        cursor.moveToPosition(index++);
        return marshaller.marshall(cursor);
    }

    @Override
    public int nextIndex() {
        return index + 1;
    }

    @Override
    public T previous() {
        cursor.moveToPosition(--index);
        return marshaller.marshall(cursor);
    }

    @Override
    public int previousIndex() {
        return index - 1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T t) {
        throw new UnsupportedOperationException();
    }
}
