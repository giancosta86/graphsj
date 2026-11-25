package info.gianlucacosta.graphsj.scenarios.classic.runtime

import info.gianlucacosta.eighthbridge.core.facets.{HasLabel, HasLabelSettable, HasNameSettable, HasSelectedSettable, HasStyleClasses}
import info.gianlucacosta.eighthbridge.fx.VisualVertex
import info.gianlucacosta.eighthbridge.fx.facets.HasCenterSettable
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm

abstract class ClassicRuntimeVertex(algorithm: ClassicAlgorithm)
  extends VisualVertex
    with HasCenterSettable
    with HasSelectedSettable
    with HasNameSettable
    with HasLabel
    with HasStyleClasses {

  override def styleClasses: Set[String] = {
    if (algorithm.partialSolutionVertexes.contains(this.asInstanceOf[algorithm.RuntimeVertex]))
      Set("partialSolution")
    else if (algorithm.fullSolutionVertexes.contains(this.asInstanceOf[algorithm.RuntimeVertex]))
      Set("fullSolution")
    else
      Set()
  }
}

