package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.eighthbridge.core.facets.{HasLabel, HasWeightBoundedSettable}
import info.gianlucacosta.graphsj.scenarios.classic.design.ClassicDesignEdge

class PrimDesignEdge
  extends ClassicDesignEdge
  with HasWeightBoundedSettable
  with HasLabel {
  override def label: String =
    super[HasWeightBoundedSettable].toString
}