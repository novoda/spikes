import android.database.Cursor
import novoda.android.typewriter.cursor.{CursorListIterator, MyObject, CursorList}

package object cursor {

  object CursorListIterator {
    def apply(cursor: Cursor) = new CursorListIterator[Object](cursor, null, 0)
    def apply(cursor: Cursor, index: Int) = new CursorListIterator(cursor, null, index)
  }

  object TypedCursor {
    def apply(cursor: Cursor) = new CursorList(cursor, classOf[MyObject])
  }
}
