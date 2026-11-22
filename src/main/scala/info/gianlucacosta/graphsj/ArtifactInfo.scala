package info.gianlucacosta.graphsj

import info.gianlucacosta.graphsj.ArtifactInfo.getClass

import java.util.Properties

//TODO! Move this to Helios-core
// !
abstract class ArtifactInfoReader {
  private val properties =
    new Properties() {
      load(
        getClass.getResourceAsStream("ArtifactInfo.properties")
      )
    }

  val name: String =
    properties.getProperty("name")

  val version: String =
    properties.getProperty("version")

  val title: String =
    s"${name} ${version}"

  val copyrightYears: String =
    properties.getProperty("copyrightYears")

  val copyrightHolder: String =
    properties.getProperty("copyrightHolder")

  val license: String =
    properties.getProperty("license")

  val website: String =
    properties.getProperty("website")

  val release: Boolean =
    !version.contains("SNAPSHOT")
}

object ArtifactInfo extends ArtifactInfoReader