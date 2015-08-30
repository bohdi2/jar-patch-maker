package org.bodhi.tools.patcher

import java.io.File

import org.bohdi.tools.patcher.CommandLineParser0

import org.scalatest._


class CommandLineParserTest extends FlatSpec with Matchers {

  behavior of "A CommandLineParser0"
  it should "do stuff" in {
    val args = CommandLineParser0.parse(Array[String]("-o", "a.jar", "b.jar", "-n", "c.jar", "-p", "p.jar"))
    args shouldBe 'isDefined

    List("a.jar", "b.jar") should equal(args.get.oldJars)
    List("c.jar") should equal(args.get.newJars)
    "p.jar" should equal(args.get.patch)
  }





  //def file(s: String) = new File(s)
  //def files(ss: String*) = ss.map(file)

}
