package org.bohdi.tools

import java.io.File

package object patcher {

  case class Config(patch: File = new File("."),
                    oldJars: Seq[File] = Seq(),
                    newJars: Seq[File] = Seq(),
                    name: String = "",
                    baseVersion: String = "",
                    patchVersion: String = "")
}
