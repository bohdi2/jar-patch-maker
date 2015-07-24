package org.bohdi.tools.patcher

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile

object Entry {
  def create(jarFile: JarFile, jarEntry: JarEntry): Entry = {
    val byteStream: ByteArrayOutputStream = new ByteArrayOutputStream
    IoUtil.copy(jarFile.getInputStream(jarEntry), byteStream)
    byteStream.close()

    new Entry(jarEntry, byteStream.toByteArray)
  }
}

class Entry(jarEntry: JarEntry, bytes: Array[Byte]) {

  def getJarEntry: JarEntry = jarEntry

  def getInputStream: InputStream = new ByteArrayInputStream(bytes)
}