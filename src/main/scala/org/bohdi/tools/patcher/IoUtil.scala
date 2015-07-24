package org.bohdi.tools.patcher

import java.io.InputStream
import java.io.OutputStream

object IoUtil {
  def copy(in: InputStream, out: OutputStream) {
    var c: Int = 0
    while ((({
      c = in.read; c
    })) != -1) {
      out.write(c)
    }
    in.close
  }
}