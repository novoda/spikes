package content

import novoda.android.typewriter.TypeWriterSpec
import novoda.android.typewriter.content.TypedResolver
import android.content.ContentResolver
import novoda.android.typewriter.cursor.MyObject
import android.net.Uri

import org.mockito.Matchers._
import org.mockito.Mockito._
import android.database.Cursor

class TypedResolverSpec extends TypeWriterSpec {

  "A typed resolver" should {
    "resolve the type correctly" in {
      val cr = mock[ContentResolver]
      val cursor = mock[Cursor]
      val tr = new TypedResolver(cr)

      when(cursor.moveToPosition( anyInt())) thenReturn true

      when(cr.query(any(classOf[Uri]), any(classOf[Array[String]]), anyString, any(classOf[Array[String]]), anyString)).thenReturn(cursor)
      when(cursor.getColumnNames).thenReturn(new Array[String](0))

      tr.get(Uri.parse("content://someuri"), classOf[MyObject])
      verify(cursor).moveToPosition(0)
    }
  }

}