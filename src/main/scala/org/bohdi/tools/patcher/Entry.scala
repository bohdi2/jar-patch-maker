package org.bohdi.tools.patcher

import java.io._
import java.util.jar.JarEntry
import java.util.jar.JarFile
import collection.JavaConversions.enumerationAsScalaIterator

case class Id(jarFile: JarFile, jarEntry: JarEntry) {
  def inputStream = jarFile.getInputStream(jarEntry)

  def sha1 = Sha1.calculateHash(inputStream)

  def name = jarEntry.getName

  def hash = s"$name/$sha1"
}

object Jars {

  def load(files: Seq[File]): Map[String, Id]= {
    val ids = for {
      file <- files
      jarFile = new JarFile(file)
      entry <- jarFile.entries()
      id = Id(jarFile, entry)
    } yield id.hash -> id

  ids.toMap
  }
}








