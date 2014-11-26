package cursor

import novoda.android.typewriter.TypeWriterSpec
import android.database.Cursor
import org.mockito.Mockito._
import org.mockito.Matchers._
import novoda.android.typewriter.cursor.{MyObject, CursorList}
import novoda.android.typewriter.cursor.CursorList.CursorListException

class CursorListSpec extends TypeWriterSpec {

  "A cursor list " should {
    "throw an exception if cursor is empty" in {
      val cursor = mock[Cursor]
      when(cursor.moveToPosition(anyInt())).thenReturn(false)
      val cl = new CursorList(cursor, classOf[MyObject])

      evaluating(cl.get(10)) should produce[CursorListException]
    }
  }
}
