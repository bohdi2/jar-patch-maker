package org.bohdi.tools.patcher

import java.io.File
import java.util.jar.JarFile
import scala.util.matching.Regex

class Extractor(pattern: Regex) {

  def getChanges(oldFilenames: Seq[File], newFilenames: Seq[File]): Seq[Entry] = {
    val e1 = getEntries(oldFilenames)
    val e2 = getEntries(newFilenames)

    e2.keySet.diff(e1.keySet).flatMap(hash => e2.get(hash)).toSeq
  }

  private def getEntries(filenames: Seq[File]): Map[String, Entry] = {
    var result = Map[String, Entry]()

    for (filename <- filenames) {
      System.out.println("Extracting: " + filename)
      val jarFile = new JarFile(filename)
      import scala.collection.JavaConversions._

      val jarEntries = enumerationAsScalaIterator(jarFile.entries)

      for (jarEntry <- jarEntries) {
        jarEntry.getName match {
          case pattern(_*) =>
            val hash = Sha1.calculateHash(jarFile.getInputStream(jarEntry))
            result = result.updated(hash, Entry.create(jarFile, jarEntry))

          case _ =>
        }
      }
      jarFile.close()
    }
    result
  }
}