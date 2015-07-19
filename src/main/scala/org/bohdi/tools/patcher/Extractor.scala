package org.bohdi.tools.patcher

import java.util.Enumeration
import java.util.HashMap
import java.util.Map
//import java.util.Set
import java.util.jar.JarEntry
import java.util.jar.JarFile
import scala.util.matching.Regex

class Extractor(pattern: Regex) {


  @throws(classOf[Exception])
  def getChanges(oldFilenames: Set[String], newFilenames: Set[String]): List[Entry] = {
    val result = List[Entry]()
    val e1: Map[String, Entry] = getEntries(oldFilenames)
    val e2: Map[String, Entry] = getEntries(newFilenames)
    import scala.collection.JavaConversions._
    for (hash <- SetUtil.difference(e2.keySet, e1.keySet)) {
      result.add(e2.get(hash))
    }
    result
  }

  @throws(classOf[Exception])
  private def getEntries(filenames: Set[String]): Map[String, Entry] = {
    val result: Map[String, Entry] = new HashMap[String, Entry]
    import scala.collection.JavaConversions._
    for (filename <- filenames) {
      System.out.println("Extracting: " + filename)
      val jarFile: JarFile = new JarFile(filename)
      val jarEntries: Enumeration[_ <: JarEntry] = jarFile.entries
      while (jarEntries.hasMoreElements) {
        val jarEntry: JarEntry = jarEntries.nextElement
        val name: String = jarEntry.getName
        System.err.println("getEntries: " + name)
        name match {
          case pattern(_*) =>
            val hash: String = Sha1.calculateHash(jarFile.getInputStream(jarEntry))
            result.put(hash, Entry.create(jarFile, jarEntry))
        }
      }
      jarFile.close
    }
    result
  }
}