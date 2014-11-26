package novoda.android.typewriter.cursor;

import android.database.Cursor;

public interface CursorMarshaller<T> {
    T marshall(Cursor cursor);
}
