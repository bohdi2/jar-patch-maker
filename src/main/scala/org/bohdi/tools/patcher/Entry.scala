package org.bohdi.tools.patcher

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile

object Entry {
  @throws(classOf[Exception])
  def create(jarFile: JarFile, jarEntry: JarEntry): Entry = {
    val byteStream: ByteArrayOutputStream = new ByteArrayOutputStream
    IoUtil.copy(jarFile.getInputStream(jarEntry), byteStream)
    byteStream.close()

    new Entry(jarEntry, byteStream.toByteArray)
  }
}

class Entry(jarEntry: JarEntry, bytes: Array[Byte]) {

  def getJarEntry: JarEntry = jarEntry

  @throws(classOf[IOException])
  def getInputStream: InputStream = new ByteArrayInputStream(bytes)



}