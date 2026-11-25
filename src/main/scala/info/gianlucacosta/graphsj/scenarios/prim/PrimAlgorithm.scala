package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.eighthbridge.core.facets.Weight
import info.gianlucacosta.eighthbridge.fx.VisualGraph
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm
import info.gianlucacosta.graphsj.scenarios.classic.runtime.{ClassicRuntimeLink, ClassicRuntimeVertex}
import info.gianlucacosta.graphsj.sdk.OutputConsole

import scala.language.implicitConversions


class PrimAlgorithm(verbose: Boolean) extends ClassicAlgorithm(verbose) {
  override type DesignVertex =
    PrimDesignVertex

  override type DesignLink =
    PrimDesignEdge

  override type RuntimeVertex =
    PrimRuntimeVertex

  override type RuntimeLink =
    PrimRuntimeEdge

  var treeWeight: Double =
    _

  var treeVertexes: Set[V] =
    _

  var treeEdges: Set[L] =
    _

  var fringe: List[V] =
    _

  var closestTreeVertexByVertex: Map[V, V] =
    _

  var minDistanceFromTreeByVertex: Map[V, Weight] =
    _

  override protected def createRuntimeVertex(designVertex: PrimDesignVertex): PrimRuntimeVertex =
    new PrimRuntimeVertex(this)

  override protected def createRuntimeLink(designLink: PrimDesignEdge): PrimRuntimeEdge =
    new PrimRuntimeEdge(this)

  override protected def onInit(graph: RuntimeGraph, console: OutputConsole): (RuntimeGraph, Boolean) = {
    askForStartVertex(graph) match {
      case Some(startVertex) =>
        treeVertexes =
          Set[V](startVertex)

        treeEdges =
          Set[L]()

        fringe = (graph.vertexes - startVertex)
          .toList
          .sortBy(_.name)

        closestTreeVertexByVertex =
          fringe
            .map(_ -> startVertex)
            .toMap

        minDistanceFromTreeByVertex =
          fringe
            .map(vertex =>
              vertex -> graph.getMinWeightBetween(vertex, startVertex)
            )
            .toMap

        (graph, true)
      case None =>
        (graph, false)
    }
  }

  override protected def onMainStep(stepIndex: Int, graph: RuntimeGraph, console: OutputConsole): (RuntimeGraph, Boolean) = {
    if (fringe.isEmpty) {
      return (graph, false)
    }

    val (nextTreeVertex, nextTreeEdge): (V, L) =
      fringe
        .map(vertex => {
          val closestTreeVertex =
            closestTreeVertexByVertex(vertex)

          graph.getMinLinkBetween(vertex, closestTreeVertex)
            .map(vertex -> _)
        })
        .collect {
          case Some((vertex, bestEdge)) =>
            (vertex, bestEdge)
        }
        .minBy(_._2.weight)

    treeVertexes +=
      nextTreeVertex

    treeEdges +=
      nextTreeEdge

    fringe =
      fringe.filter(_ != nextTreeVertex)

    fringe.foreach(vertex => {
      val minDistanceFromNextTreeVertex =
        graph.getMinWeightBetween(vertex, nextTreeVertex)

      if (minDistanceFromNextTreeVertex < minDistanceFromTreeByVertex(vertex)) {
        closestTreeVertexByVertex +=
          vertex -> nextTreeVertex

        minDistanceFromTreeByVertex +=
          vertex -> minDistanceFromNextTreeVertex
      }
    })

    (graph, true)
  }

  override protected def onExpectedEnd(stepIndex: Int, graph: RuntimeGraph, console: OutputConsole): RuntimeGraph = {
    treeWeight =
      treeEdges
        .map(_.weight)
        .sum

    graph
  }

  override def partialSolutionLinks: Set[PrimRuntimeEdge] =
    treeEdges
}
