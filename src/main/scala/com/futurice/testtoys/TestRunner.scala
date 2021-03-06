package com.futurice.testtoys

import java.io.{File, IOException, PrintWriter, StringWriter}

import scala.collection.mutable.ArrayBuffer
/**
  * Created by arau on 4.6.2016.
  */
class TestRunner(rootPath:String, tests:Seq[TestSuite]) {

  val rootDir = new File(rootPath)

  def printHelp {
    println()
    println("usage: [options...] [tests...]")
    println()
    println("options:")
    println()
    println("  -i    interactive mode")
    println("  -f    automatic freeze")
    println("  -n    never freeze")
    println("  -l    list tests")
    println()

  }

  def exec(args: Array[String]) {
    if (args.contains("-h")) {
      printHelp
    } else {
      val fail= -1
      val list = -2

      val selecttion =
        args.filter(!_.startsWith("-"))
      val config =
        args.filter(_.startsWith("-")).map {
          _ match {
            case "-i" => TestTool.INTERACTIVE
            case "-f" => TestTool.AUTOMATIC_FREEZE
            case "-n" => TestTool.NEVER_FREEZE
            case "-l" => list
            case x =>
              println("unknown flag " + x)
              fail
          }
        }.fold(TestTool.INTERACTIVE)((f, s) => s)

      if (config == fail) {
        printHelp
      } else if (config == list) {
        tests.map { t =>
          println(t.name)
        }
      } else if (config != -1) {
        exec(args, config)
      }
    }
  }

  def exec(selection: Array[String], config: Int) {
    val selected : Seq[(TestSuite, String)] =
      if (selection.isEmpty) {
        tests.map(t => (t, ""))
      } else {
        selection.flatMap { s =>
          val path = s.split('/')
          val t : Seq[TestSuite] =
            tests.filter { e =>
              val epath = e.name.split('/')
              epath.startsWith(path) || path.startsWith(epath)
            }
          if (t.isEmpty) println(s + " not found")
          t.map(e => (e, path.view(e.name.split('/').size, path.size).toSeq.mkString("/"))) : Seq[(TestSuite, String)]
        }
      }
    var ok = true
    val i = selected.iterator
    while (ok && i.hasNext) {
      val e = i.next()
      ok = e._1.run(e._2, rootDir, config)
    }
  }

}


object TestRunner {
  def apply(rootPath:String, tests:Seq[TestSuite]) = {
    new TestRunner(rootPath, tests)
  }
}