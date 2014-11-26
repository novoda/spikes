package util

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import novoda.android.typewriter.util.StringUtil


class StringUtilSpec extends WordSpec with ShouldMatchers {
  "A string" should {

    "be transformed into camel case" in {
      Map(
        "hello_world1" -> "HelloWorld1",
        "_id" -> "Id",
        "hello world2" -> "HelloWorld2",
        "HelloWorld3" -> "HelloWorld3",
        "hello__world4" -> "HelloWorld4",
        "hello____world5" -> "HelloWorld5",
        "helloWorld6" -> "HelloWorld6",
        "hello_World7" -> "HelloWorld7"
      ).foreach(kv => StringUtil.camelify(kv._1) should be(kv._2))
    }

    "be transformed into a get method" in {
      Map(
        "hello_world" -> "getHelloWorld"
      ).foreach(kv => StringUtil.asCamelifyGetMethod(kv._1) should be(kv._2))
    }

    "be transformed into a set method" in {
      Map(
        "hello_world" -> "setHelloWorld"
      ).foreach(kv => StringUtil.asCamelifySetMethod(kv._1) should be(kv._2))
    }

    "be transformed into snake case" in {
      Map(
        "hello_world1" -> "HelloWorld1",
        "hello_world" -> "hello_world"
      ).foreach(kv => StringUtil.snakify(kv._2) should be(kv._1))
    }
  }
}