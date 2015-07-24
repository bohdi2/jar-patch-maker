package org.bohdi.tools.patcher

import java.util.jar.Attributes.Name._
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object CreatePatchFile {
  private val IMPLEMENTATION_PATCH: Attributes.Name = new Attributes.Name("Implementation-Patch")

  def main(args: Array[String]) {

    case class Config(patch: File = new File("."),
                      old: Seq[File] = Seq(),
                      `new`: Seq[File] = Seq())

    val parser = new scopt.OptionParser[Config]("scopt") {
      head("scopt", "3.x")

      opt[File]('p', "patch") required() valueName "<file>" action { (x, c) =>
        c.copy(patch = x)
      } text "out is a required file property"


      opt[Seq[File]]('o', "old") valueName "<jar1>,<jar2>..." action { (x, c) =>
        c.copy(old = x)
      } text "old jars to include"

      opt[Seq[File]]('n', "new") valueName "<jar1>,<jar2>..." action { (x, c) =>
        c.copy(`new` = x)
      } text "new jars to include"

    }

    // parser.parse returns Option[C]
    parser.parse(args, Config()) match {
      case Some(config) =>
        val extractor = new Extractor(".*\\.class$".r)
        createPatch(config.patch,
          createManifest("", "", ""),
          extractor.getChanges(config.old, config.`new`))

      case None =>
      // arguments are bad, error message will have been displayed
    }

  }

  private def createManifest(name: String, baseVersion: String, patchVersion: String): Manifest = {
    println("createManifest")
    val fmt = DateTimeFormat.forPattern("MMMM dd yyyy")
    val today = fmt.print(new DateTime)
    val manifest = new Manifest
    var attributes = manifest.getMainAttributes
    attributes.put(MANIFEST_VERSION, "1.0")
    attributes = new Attributes
    attributes.put(IMPLEMENTATION_PATCH, patchVersion)
    attributes.put(IMPLEMENTATION_TITLE, name)
    attributes.put(IMPLEMENTATION_VENDOR, "ICAP Development")
    attributes.put(IMPLEMENTATION_VERSION, name + " " + baseVersion + " " + today)
    attributes.put(SPECIFICATION_TITLE, name + " Patch")
    attributes.put(SPECIFICATION_VENDOR, "ICAP")
    attributes.put(SPECIFICATION_VERSION, baseVersion)
    manifest.getEntries.put("BrokerNet", attributes)
    manifest
  }

  def createPatch(filename: File, manifest: Manifest, entries: Seq[Entry]) {
    println("createPatch: " + filename)
    val newJar = new JarOutputStream(new FileOutputStream(filename), manifest)
    for (entry <- entries) {
      System.out.println("Entry " + entry)
      newJar.putNextEntry(entry.getJarEntry)
      IoUtil.copy(entry.getInputStream, newJar)
      newJar.closeEntry()
    }
    newJar.close()
  }
}