package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.graphsj.scenarios.classic.ClassicWeightedLink
import info.gianlucacosta.graphsj.scenarios.classic.runtime.ClassicRuntimeLink

class PrimRuntimeEdge(algorithm: PrimAlgorithm)
  extends ClassicRuntimeLink(algorithm)
  with ClassicWeightedLink
