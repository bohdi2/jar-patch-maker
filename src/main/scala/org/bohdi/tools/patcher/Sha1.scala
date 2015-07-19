package org.bohdi.tools.patcher

import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object Sha1 {
  @throws(classOf[Exception])
  def calculateHash(stream: InputStream): String = {
    val sha1: MessageDigest = MessageDigest.getInstance("SHA1")
    calculateHash(sha1, stream)
  }

  @throws(classOf[Exception])
  private def calculateHash(algorithm: MessageDigest, stream: InputStream): String = {
    val dis: DigestInputStream = new DigestInputStream(stream, algorithm)
    while (dis.read != -1) {}


    val hash: Array[Byte] = algorithm.digest
    byteArray2Hex(hash)
  }

  private def byteArray2Hex(hash: Array[Byte]): String = {
    hash.foldLeft("")((accum, b) => accum + f"$b%2x")
  }
}