package info.gianlucacosta.graphsj.icons

import java.io.InputStream
import java.net.URL

//TODO! Move to helios-core!
abstract class MainIconReader {
  def get(size: Int): InputStream =
    getClass.getResourceAsStream(getMainIconName(size))

  def getUrl(size: Int): URL =
    getClass.getResource(getMainIconName(size))

  private def getMainIconName(size: Int): String =
    s"mainIcon${size}.png"
}

//TODO! Generate the icon .png files!
object MainIcon extends MainIconReader