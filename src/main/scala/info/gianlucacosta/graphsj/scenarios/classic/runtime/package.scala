package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.Graph
import info.gianlucacosta.graphsj.scenarios.classic.design.{ClassicDesignLink, ClassicDesignVertex}

package object runtime {
  implicit class ClassicDesignGraphExtensions[
    DesignV <: ClassicDesignVertex,
    DesignL <: ClassicDesignLink
  ](
    self: Graph[DesignV, DesignL]
  ) {
    def createRuntimeGraph[
      V <: ClassicRuntimeVertex,
      L <: ClassicRuntimeLink
    ](
        runtimeVertexCreator: (DesignV) => V,
        runtimeLinkCreator: (DesignV, DesignV, DesignL) => L
    ): Graph[V, L] = {
      var runtimeGraph: Graph[V, L] = (
        if (self.isDirected)
          Graph.createDirected()
        else
          Graph.createUndirected()
        ).asInstanceOf[Graph[V, L]]

      var runtimeVertexByDesignVertex: Map[DesignV, V] =
        Map[DesignV, V]()

      self.vertexes.foreach(designVertex => {
        val runtimeVertex =
          runtimeVertexCreator(designVertex)
            .copyFacetsFrom(designVertex)

        runtimeVertexByDesignVertex +=
          designVertex -> runtimeVertex

        runtimeGraph =
          runtimeGraph.addVertex(runtimeVertex)
      })

      self.links.foreach(designLink => {
        val (firstDesignAnchor, secondDesignAnchor): (DesignV, DesignV) =
          self.anchorsByLink(designLink).toPair

        val runtimeAnchors =
          (
            runtimeVertexByDesignVertex(firstDesignAnchor),
            runtimeVertexByDesignVertex(secondDesignAnchor)
          )

        val runtimeLink: L =
          runtimeLinkCreator(firstDesignAnchor, secondDesignAnchor, designLink)
            .copyFacetsFrom(designLink)

        runtimeGraph =
          runtimeGraph.addLink(runtimeAnchors, runtimeLink)
      })

      runtimeGraph
    }
  }
}
