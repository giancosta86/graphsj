//TODO! Move this entire package to the SDK!
package info.gianlucacosta.graphsj.scenarios.classic.design

import info.gianlucacosta.eighthbridge.core.facets._
import info.gianlucacosta.eighthbridge.fx.VisualVertex
import info.gianlucacosta.eighthbridge.fx.facets.HasCenterSettable

trait ClassicDesignVertex
  extends VisualVertex
    with HasCenterSettable
    with HasSelectedSettable
    with HasNameSettable
    with HasLabel {
  override def label: String =
    name
}