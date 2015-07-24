package org.bohdi.tools.patcher

import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object Sha1 {
  def calculateHash(stream: InputStream): String = {
    calculateHash(MessageDigest.getInstance("SHA1"), stream)
  }

  private def calculateHash(algorithm: MessageDigest, stream: InputStream): String = {
    val dis = new DigestInputStream(stream, algorithm)
    while (dis.read != -1) {}

    byteArray2Hex(algorithm.digest)
  }

  private def byteArray2Hex(hash: Array[Byte]): String = {
    hash.foldLeft("")((accum, b) => accum + f"$b%2x")
  }
}