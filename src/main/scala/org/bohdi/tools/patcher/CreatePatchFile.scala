package org.bohdi.tools.patcher

import java.util.jar.Attributes.Name._
import java.io.{InputStream, OutputStream, File, FileOutputStream}
import java.util.jar.{Attributes, JarOutputStream, Manifest}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat



object CreatePatchFile {
  private val IMPLEMENTATION_PATCH: Attributes.Name = new Attributes.Name("Implementation-Patch")

  def main(args: Array[String]) {

    val parser = CommandLineParser0

    parser.parse(args) match {
      case Some(arguments) =>
        createPatch(arguments, createManifest(arguments))

      case None =>
      // arguments are bad, error message will have been displayed
    }

  }

  private def createManifest(arguments: Arguments): Manifest = {
    val fmt = DateTimeFormat.forPattern("MMMM dd yyyy")
    val today = fmt.print(new DateTime)
    val manifest = new Manifest
    var attributes = manifest.getMainAttributes
    attributes.put(MANIFEST_VERSION, "1.0")
    attributes = new Attributes
    attributes.put(IMPLEMENTATION_PATCH, arguments.patchVersion)
    attributes.put(IMPLEMENTATION_TITLE, arguments.name)
    attributes.put(IMPLEMENTATION_VENDOR, "ICAP Development")
    attributes.put(IMPLEMENTATION_VERSION, arguments.name + " " + arguments.baseVersion + " " + today)
    attributes.put(SPECIFICATION_TITLE, arguments.name + " Patch")
    attributes.put(SPECIFICATION_VENDOR, "ICAP")
    attributes.put(SPECIFICATION_VERSION, arguments.baseVersion)
    manifest.getEntries.put("BrokerNet", attributes)
    manifest
  }

  def createPatch(arguments: Arguments, manifest: Manifest) {
    val oldIds = Jars.load(arguments.oldJars)
    val newIds = Jars.load(arguments.newJars)

    new File(arguments.patch).createNewFile()
    val jos = new JarOutputStream(new FileOutputStream(arguments.patch), manifest)

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