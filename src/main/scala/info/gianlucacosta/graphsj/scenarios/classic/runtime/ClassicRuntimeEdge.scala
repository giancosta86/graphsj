package info.gianlucacosta.graphsj.scenarios.classic.runtime

import info.gianlucacosta.eighthbridge.fx.VisualEdge
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm

abstract class ClassicRuntimeEdge(algorithm: ClassicAlgorithm)
  extends ClassicRuntimeLink(algorithm)
    with VisualEdge
