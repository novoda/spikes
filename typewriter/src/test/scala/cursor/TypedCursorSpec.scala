package cursor

import novoda.android.typewriter.TypeWriterSpec
import android.database.Cursor
import novoda.android.typewriter.cursor.{MyObject, CursorList}

import org.mockito.Mockito._

class TypedCursorSpec extends TypeWriterSpec {

  "a typed cursor" should {

    "give empty count" in {
      val cursor = mock[Cursor]
      when(cursor.getCount).thenReturn(0)
      when(cursor.getColumnNames).thenReturn(List("one").toArray)
      TypedCursor(cursor).size should be(0)
    }

    "give correct count" in {
      val cursor = mock[Cursor]
      when(cursor.getCount).thenReturn(5)
      when(cursor.getColumnNames).thenReturn(List("one").toArray)
      TypedCursor(cursor).size should be(5)
    }

    "modifiable methods" should {
      "throw an exception" in {
        val c = mock[Cursor]
        when(c.getColumnNames).thenReturn(List("one").toArray)
        val tc = new CursorList(c, classOf[MyObject])

        evaluating(tc.add(null)) should produce[RuntimeException]
        evaluating(tc.add(-1, null)) should produce[RuntimeException]
        evaluating(tc.addAll(null)) should produce[RuntimeException]
        evaluating(tc.addAll(-1, null)) should produce[RuntimeException]
        evaluating(tc.clear()) should produce[RuntimeException]
        evaluating(tc.contains(null)) should produce[RuntimeException]
        evaluating(tc.containsAll(null)) should produce[RuntimeException]
        evaluating(tc.indexOf(null)) should produce[RuntimeException]
        evaluating(tc.lastIndexOf(null)) should produce[RuntimeException]
        evaluating(tc.remove()) should produce[RuntimeException]
        evaluating(tc.remove(null)) should produce[RuntimeException]
        evaluating(tc.removeAll(null)) should produce[RuntimeException]
        evaluating(tc.retainAll(null)) should produce[RuntimeException]
        evaluating(tc.set(1, null)) should produce[RuntimeException]
      }
    }

  }
}