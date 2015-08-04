package org.bohdi.tools.patcher


class Extractor(regex: String) {
/*
  def getChanges(config: Config): Seq[Entry] = {
    val oldEntries = getEntries(config.oldJars)
    val newEntries = getEntries(config.newJars)

    val newKeys = newEntries.keySet -- oldEntries.keySet

    newKeys.map(newEntries(_)).toSeq
  }

  private def getEntries(filenames: Seq[File]): Map[String, Entry] = {
    var result = Map[String, Entry]()

    for (filename <- filenames) {
      System.out.println("Extracting: " + filename)
      val jar = new Jar(filename)
      val classEntries = jar.filter(_.getName matches regex)

      val hashes = for {
        entry <- classEntries
      } yield (hash(jar, entry), entry)

      //result = result ++ hashes
    }

    result
  }

  private def hash(jar: Jar, entry: JarEntry): Option[String] = {
    jar.withEntryStream(entry.getName())(_.map(Sha1.calculateHash(_)))
  }
  */
}