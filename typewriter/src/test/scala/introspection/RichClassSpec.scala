package introspection

import org.scalatest.WordSpec
import novoda.android.typewriter.introspection.RichClass
import novoda.android.typewriter.annotation.Mapper
import novoda.android.typewriter.cursor.MyObject
import org.scalatest.matchers._
import novoda.android.typewriter.introspection.RichClass.RichClassException
import scala.beans.BeanProperty

trait RichClassMatcher {

  def method(expectedValue: String) =

    new HavePropertyMatcher[RichClass[_], String] {
      def apply(klass: RichClass[_]) =
        HavePropertyMatchResult(
          klass.hasMethod(expectedValue),
          "method",
          expectedValue,
          klass.getClass.getMethods.toString
        )
    }
}

class TestObject(
                  @BeanProperty var nameCamelCase: String,
                  @BeanProperty var name: String,
                  @BeanProperty var id: Int,
                  @BeanProperty var short: Short,
                  @BeanProperty var long: Long,
                  @BeanProperty var float: Float,
                  @BeanProperty @Mapper("some_value") var mappedString: String
                  ) {
  @Mapper("some_value")
  def mapped(s: String) {}
}

class RichClassSpec extends WordSpec with ShouldMatchers with RichClassMatcher {
  "a rich class" should {

    "find correct setter" in {
      new RichClass(classOf[TestObject]).setter("name") should be(getMethod("setName", classOf[String]))
      new RichClass(classOf[TestObject]).setter("name_camel_case") should be(getMethod("setNameCamelCase", classOf[String]))
    }

    "throw an exception if getMethod not found" in {
      evaluating(
        new RichClass(classOf[TestObject]).setter("name_not_found")
      ) should produce[RichClassException]
    }

    "find correct type setter" in {
      new RichClass(classOf[TestObject]).setter("name") should be(getMethod("setName", classOf[String]))
      new RichClass(classOf[TestObject]).setter("id") should be(getMethod("setId", classOf[Int]))
      new RichClass(classOf[TestObject]).setter("short") should be(getMethod("setShort", classOf[Short]))
      new RichClass(classOf[TestObject]).setter("long") should be(getMethod("setLong", classOf[Long]))
      new RichClass(classOf[TestObject]).setter("float") should be(getMethod("setFloat", classOf[Float]))
    }

    "map the correct setter using annotation" in {
      new RichClass(classOf[TestObject]).setter("some_value") should be(getMethod("mapped", classOf[String]))
      new RichClass(classOf[MyObject]).setter("some_value") should be(classOf[MyObject].getMethod("setTest2", classOf[String]))
    }

    "have the getMethod" in {
      List("name_camel_case", "name", "id", "short", "long", "float", "some_value").foreach(
        (name) => new RichClass(classOf[TestObject]) should have(method(name))
      )
    }

    def getMethod(mn: String, c: Class[_]) = {
      classOf[TestObject].getMethod(mn, c)
    }

  }
}