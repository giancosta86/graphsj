package info.gianlucacosta.graphsj.scenarios.classic.runtime

import info.gianlucacosta.eighthbridge.core.directed.HasDefaultBarbs
import info.gianlucacosta.eighthbridge.fx.{VisualArc, VisualEdge}
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm

class ClassicRuntimeEdge(algorithm: ClassicAlgorithm)
  extends ClassicRuntimeLink(algorithm)
    with VisualEdge
