package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.Link
import info.gianlucacosta.eighthbridge.core.facets.{HasLabel, HasWeightBoundedSettable}
import info.gianlucacosta.eighthbridge.fx.VisualLink
import info.gianlucacosta.graphsj.scenarios.classic.design.ClassicDesignLink

trait ClassicWeightedLink
    extends VisualLink
    with HasWeightBoundedSettable
    with HasLabel {
  override def label: String =
    super[HasWeightBoundedSettable].toString
}
