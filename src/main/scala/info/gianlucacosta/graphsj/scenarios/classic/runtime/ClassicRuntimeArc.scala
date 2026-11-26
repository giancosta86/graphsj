package info.gianlucacosta.graphsj.scenarios.classic.runtime

import info.gianlucacosta.eighthbridge.core.directed.HasDefaultBarbs
import info.gianlucacosta.eighthbridge.fx.VisualArc
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm

class ClassicRuntimeArc(algorithm: ClassicAlgorithm)
  extends ClassicRuntimeLink(algorithm)
    with VisualArc
    with HasDefaultBarbs
