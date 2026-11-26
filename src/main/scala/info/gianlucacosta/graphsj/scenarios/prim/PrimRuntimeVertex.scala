package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.graphsj.scenarios.classic.runtime.ClassicRuntimeVertex
import info.gianlucacosta.helios.mathutils.Numbers

class PrimRuntimeVertex(algorithm: PrimAlgorithm)
  extends ClassicRuntimeVertex(algorithm) {
  override def label: String = {
    algorithm.closestTreeVertexByVertex.get(this)
      .map(closestTreeVertex => {
        val minDistanceFromTree =
          algorithm.minDistanceFromTreeByVertex(this)

        s"${name} (${closestTreeVertex.name}; ${Numbers.smartString(minDistanceFromTree)})"
      })
      .getOrElse(name)
  }
}
