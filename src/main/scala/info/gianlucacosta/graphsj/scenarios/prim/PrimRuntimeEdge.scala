package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.graphsj.scenarios.classic.ClassicWeightedLink
import info.gianlucacosta.graphsj.scenarios.classic.runtime.{ClassicRuntimeEdge, ClassicRuntimeLink}

class PrimRuntimeEdge(algorithm: PrimAlgorithm)
  extends ClassicRuntimeEdge(algorithm)
  with ClassicWeightedLink
