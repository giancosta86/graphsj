package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.eighthbridge.core.facets.HasWeightBoundedSettable
import info.gianlucacosta.graphsj.scenarios.classic.runtime.ClassicRuntimeLink

class PrimRuntimeEdge(algorithm: PrimAlgorithm)
  extends ClassicRuntimeLink(algorithm)
  with HasWeightBoundedSettable
