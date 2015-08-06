package org.bohdi.tools.patcher

import java.util.jar.Attributes.Name._
import java.io.{InputStream, OutputStream, File, FileOutputStream}
import java.util.jar.{Attributes, JarOutputStream, Manifest}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


object CreatePatchFile {
  private val IMPLEMENTATION_PATCH: Attributes.Name = new Attributes.Name("Implementation-Patch")

  def main(args: Array[String]) {

    val parser = new scopt.OptionParser[Config]("scopt") {
      head("scopt", "3.x")

      opt[File]('p', "patch") required() valueName "<file>" action { (x, c) =>
        c.copy(patch = x)
      } text "out is a required file property"


      opt[Seq[File]]('o', "old") valueName "<jar1>,<jar2>..." action { (x, c) =>
        c.copy(oldJars = x)
      } text "old jars to include"

      opt[Seq[File]]('n', "new") valueName "<jar1>,<jar2>..." action { (x, c) =>
        c.copy(newJars = x)
      } text "new jars to include"

    }

    // parser.parse returns Option[C]
    parser.parse(args, Config()) match {
      case Some(config) =>
        createPatch(config, createManifest(config))

      case None =>
      // arguments are bad, error message will have been displayed
    }

  }

  private def createManifest(config: Config): Manifest = {
    val fmt = DateTimeFormat.forPattern("MMMM dd yyyy")
    val today = fmt.print(new DateTime)
    val manifest = new Manifest
    var attributes = manifest.getMainAttributes
    attributes.put(MANIFEST_VERSION, "1.0")
    attributes = new Attributes
    attributes.put(IMPLEMENTATION_PATCH, config.patchVersion)
    attributes.put(IMPLEMENTATION_TITLE, config.name)
    attributes.put(IMPLEMENTATION_VENDOR, "ICAP Development")
    attributes.put(IMPLEMENTATION_VERSION, config.name + " " + config.baseVersion + " " + today)
    attributes.put(SPECIFICATION_TITLE, config.name + " Patch")
    attributes.put(SPECIFICATION_VENDOR, "ICAP")
    attributes.put(SPECIFICATION_VERSION, config.baseVersion)
    manifest.getEntries.put("BrokerNet", attributes)
    manifest
  }

  def createPatch(config: Config, manifest: Manifest) {
    val oldIds = Jars.load(config.oldJars)
    val newIds = Jars.load(config.newJars)

    config.patch.createNewFile()
    val jos = new JarOutputStream(new FileOutputStream(config.patch), manifest)

    val changedHashes = newIds.keySet diff oldIds.keySet
    val patchIds = newIds.filterKeys(changedHashes.contains)

    println(s"Old: ${oldIds.size}, new: ${newIds.size}, Patched: ${patchIds.size}")

    for (id <- patchIds.values) {
      println(s"Including ${id.name}")
      jos.putNextEntry(id.jarEntry)
      transfer(id.inputStream, jos)
    }

    jos.close()
  }

    def transfer(in: InputStream, out: OutputStream) = {
      val buf = new Array[Byte](10240)

      def loop(): Unit = in.read(buf, 0, buf.length) match {
        case -1 => in.close()
        case n  => out.write(buf, 0, n) ; loop()
      }

      loop()
    }


}