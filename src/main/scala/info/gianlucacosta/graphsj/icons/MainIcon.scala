package info.gianlucacosta.graphsj.icons

import java.io.InputStream
import java.net.URL

//TODO! Generate this file, as well as the icon .png files!
object MainIcon {
  def get(size: Int): InputStream =
    getClass.getResourceAsStream(getMainIconName(size))


  def getUrl(size: Int): URL =
    getClass.getResource(getMainIconName(size))


  private def getMainIconName(size: Int): String =
    s"mainIcon${size}.png"
}