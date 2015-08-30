package org.bohdi.tools.patcher

import java.io.File

trait CommandLineParser {
  def usage: String
  def parse(args: Array[String]): Option[Arguments]
}

object CommandLineParser0 extends CommandLineParser {

  def usage = "-o files -n files -p jarfile"

  def parse(args: Array[String]): Option[Arguments] = {
    if (args.length == 0) println(usage)
    val arglist = args.toList

    def nextOption(args : Arguments, list: List[String]) : Arguments = {

      //println(list)
      def isSwitch(s : String) = s(0) == '-'

      list match {
        case Nil => args

        case "-o" :: value :: tail if !isSwitch(value) =>
          nextOption(args.copy(oldJars = args.oldJars :+ value), "-o":: tail)
        case "-o" :: value :: tail if isSwitch(value) =>
          nextOption(args, value :: tail)
        case "-n" :: value :: tail if !isSwitch(value) =>
          nextOption(args.copy(newJars = args.newJars :+ value), "-n":: tail)
        case "-n" :: value :: tail if isSwitch(value) =>
          nextOption(args, value :: tail)
        case "-p" :: value :: tail =>
          nextOption(args.copy(patch = value), tail)
        case value :: tail => println("Oooops")
          args
      }
    }

    Some(nextOption(Arguments(), arglist))
  }

}




