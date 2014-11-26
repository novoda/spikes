package cursor

import android.database.Cursor
import novoda.android.typewriter.cursor.{MyObject, ReflectionCursorMarshaller}
import novoda.android.typewriter.TypeWriterSpec

import org.mockito.Mockito._
import org.mockito.Matchers._

class CursorMarshallerSpec extends TypeWriterSpec {

  "a cursor marshaller" should {

    "throw an exception if the cursor is not moved (position -1)" in {
      val c = mock[Cursor]
      when(c.getPosition).thenReturn(-1)
      val marshaller = new ReflectionCursorMarshaller[MyObject](c, classOf[MyObject])
      evaluating {
        marshaller.marshall(c)
      } should produce[RuntimeException]
    }

    "marshall string into MyObject" in {
      val c = mock[Cursor]
      when(c.getType(anyInt)).thenReturn(Cursor.FIELD_TYPE_STRING)
      when(c.getColumnCount).thenReturn(1)
      when(c.getColumnName(anyInt())).thenReturn("test")
      when(c.getString(anyInt)).thenReturn("hello world")
      val marshaller = new ReflectionCursorMarshaller[MyObject](c, classOf[MyObject])
      val obj = marshaller.marshall(c)
      obj.test should be("hello world")
    }

    "marshall float correctly" in {
      val c = mock[Cursor]
      when(c.getType(anyInt)).thenReturn(Cursor.FIELD_TYPE_FLOAT)
      when(c.getColumnCount).thenReturn(1)
      when(c.getColumnName(anyInt())).thenReturn("myFloat")
      when(c.getFloat(anyInt)).thenReturn(1.0f)
      val marshaller = new ReflectionCursorMarshaller[MyObject](c, classOf[MyObject])
      val obj = marshaller.marshall(c)
      obj.myFloat should be(1.0f)
    }

    "marshall Integer correctly" in {
      val c = mock[Cursor]
      when(c.getType(anyInt)).thenReturn(Cursor.FIELD_TYPE_INTEGER)
      when(c.getColumnCount).thenReturn(1)
      when(c.getColumnName(anyInt())).thenReturn("myInt")
      when(c.getInt(anyInt)).thenReturn(1)
      val marshaller = new ReflectionCursorMarshaller[MyObject](c, classOf[MyObject])
      val obj = marshaller.marshall(c)
      obj.myInt should be(1)
    }

    "marshall long correctly" in {
      val c = mock[Cursor]
      when(c.getType(anyInt)).thenReturn(Cursor.FIELD_TYPE_INTEGER)
      when(c.getColumnCount).thenReturn(1)
      when(c.getColumnName(anyInt())).thenReturn("_id")
      when(c.getLong(anyInt)).thenReturn(Long.MaxValue)
      val marshaller = new ReflectionCursorMarshaller[MyObject](c, classOf[MyObject])
      val obj = marshaller.marshall(c)
      obj.id should be(Long.MaxValue)
    }

  }
}