package org.bohdi.tools.patcher

import java.util.jar.Attributes.Name._
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.DefaultParser
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

object CreatePatchFile {
  private val IMPLEMENTATION_PATCH: Attributes.Name = new Attributes.Name("Implementation-Patch")

  def main(args: Array[String]) {
    var oldFilenames = Set[String]()
    var newFilenames = Set[String]()
    var patchFilename: String = ""
    var product: String = ""
    var v1: String = ""
    var v2: String = ""
    val parser: CommandLineParser = new DefaultParser
    val options: Options = new Options

    options.addOption(Option.builder("o").required().longOpt("old").valueSeparator(' ').build)
    options.addOption(Option.builder("n").required().longOpt("new").valueSeparator(' ').build)
    options.addOption(Option.builder("f").required().longOpt("patch").valueSeparator(' ').build)


    //options.addOption(OptionBuilder.withArgName("string").withLongOpt("version1").hasArgs.withValueSeparator(' ').withDescription("old version").create("1"))
    //options.addOption(OptionBuilder.withArgName("string").withLongOpt("version2").hasArgs.withValueSeparator(' ').withDescription("new version").create("2"))
    //options.addOption(OptionBuilder.withArgName("patchfile").withLongOpt("file").hasArgs.withValueSeparator(' ').withDescription("patch jar filename").create("f"))
    //options.addOption(OptionBuilder.withArgName("list of files").withLongOpt("new").hasArgs.withValueSeparator(' ').withDescription("new jar files").create("n"))
    //options.addOption(OptionBuilder.withArgName("list of files").withLongOpt("old").hasArgs.withValueSeparator(' ').withDescription("old jar files").create("o"))
    //options.addOption(OptionBuilder.withArgName("string").withLongOpt("product").hasArgs.withValueSeparator(' ').withDescription("product name for manifest").create("p"))
    //options.addOption("h", "help", false, "print this message.")

    try {
      val line: CommandLine = parser.parse(options, args)
      if (line.hasOption("help")) {
        new HelpFormatter().printHelp("patch_maker -o oldfiles+ -n newfiles+ -f patchfile -p product -1 [oldversion] -2 [newversion]", options)
        System.exit(-1)
      }
      for (filename <- line.getOptionValues("old")) {
        oldFilenames = oldFilenames + filename
      }
      for (filename <- line.getOptionValues("new")) {
        newFilenames = newFilenames + filename
      }
      patchFilename = line.getOptionValue("file")
      product = line.getOptionValue("product")
      v1 = line.getOptionValue("version1")
      v2 = line.getOptionValue("version2")
      System.out.format("v1: %s\n", v1)
    }
    catch {
      case exp: ParseException =>
        System.out.println("Unexpected exception:" + exp.getMessage)

    }
    val patchFile: File = new File(patchFilename)
    val extractor: Extractor = new Extractor(".*\\.class$".r)
    try {
      createPatch(patchFile, createManifest(product, v1, v2), extractor.getChanges(oldFilenames, newFilenames))
    }
    catch {
      case e: Exception =>
        e.printStackTrace()

    }
  }

  private def createManifest(name: String, baseVersion: String, patchVersion: String): Manifest = {
    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("MMMM dd yyyy")
    val today: String = fmt.print(new DateTime)
    val manifest: Manifest = new Manifest
    var attributes: Attributes = manifest.getMainAttributes
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

  @throws(classOf[Exception])
  def createPatch(filename: File, manifest: Manifest, entries: List[Entry]) {
    val newJar: JarOutputStream = new JarOutputStream(new FileOutputStream(filename), manifest)
    for (entry <- entries) {
      System.out.println("Entry " + entry)
      newJar.putNextEntry(entry.getJarEntry)
      IoUtil.copy(entry.getInputStream, newJar)
      newJar.closeEntry()
    }
    newJar.close()
  }
}