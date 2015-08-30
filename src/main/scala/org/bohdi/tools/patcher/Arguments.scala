package org.bohdi.tools.patcher

case class Arguments(patch: String = "",
                     oldJars: List[String] = List(),
                     newJars: List[String] = List(),
                      name: String = "",
                      baseVersion: String = "",
                      patchVersion: String = "")




