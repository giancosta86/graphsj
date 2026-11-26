package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.eighthbridge.core.facets.Weight
import info.gianlucacosta.eighthbridge.fx.VisualGraph
import info.gianlucacosta.graphsj.scenarios.classic.ClassicAlgorithm
import info.gianlucacosta.graphsj.scenarios.classic.runtime.{ClassicRuntimeLink, ClassicRuntimeVertex}
import info.gianlucacosta.graphsj.sdk.OutputConsole

import scala.language.implicitConversions


class PrimAlgorithm(verbose: Boolean) extends ClassicAlgorithm(verbose) {
  override type DesignV =
    PrimDesignVertex

  override type DesignL =
    PrimDesignEdge

  override type V =
    PrimRuntimeVertex

  override type L =
    PrimRuntimeEdge

  //TODO! Should I use getters, or would that be over-encapsulation
  var treeWeight: Double =
    _

  var treeVertexes: Set[V] =
    _

  var treeEdges: Set[L] =
    _

  var fringe: List[V] =
    _

  var closestTreeVertexByVertex: Map[V, V] =
    Map[V, V]()

  var minDistanceFromTreeByVertex: Map[V, Weight] =
    Map[V, Weight]()

  private var nextTreeVertex: V = _


  override protected def createRuntimeVertex(designVertex: PrimDesignVertex): PrimRuntimeVertex =
    new PrimRuntimeVertex(this)

  override protected def createRuntimeLink(designLink: PrimDesignEdge): PrimRuntimeEdge =
    new PrimRuntimeEdge(this)

  override protected def printLegend(console: OutputConsole): Unit = {
    console.writeln(
      "V",
      "The graph vertexes"
    )

    console.writeln(
      "W",
      "Vertexes belonging to the tree in the current step"
    )

    console.writeln(
      "E",
      "Edges belonging to the tree in the current step"
    )

    console.writeln(
      "Vbar",
      "Vertex added to the tree in the current step"
    )
  }

  override protected def printAfterInit(graph: G, console: OutputConsole): Unit = {
    printData(graph, console)
  }

  override protected def printAfterMainStep(stepIndex: Int, graph: G, console: OutputConsole): Unit = {
    console.writeln("Vbar", nextTreeVertex.name)

    printData(graph, console)
  }

  private def printData(graph: G, console: OutputConsole): Unit = {
    console.writeln(
      "W",
      treeVertexes
        .map(_.name)
        .toList
        .sorted
    )

    console.writeln(
      "V\\W",
      (graph.vertexes -- treeVertexes)
        .map(_.name)
        .toList
        .sorted
    )

    console.writeln(
      "E",
      treeEdges.map(edge =>
        graph.getAnchorPair(edge)
          .get
          .sortedNamePair
      )
    )
  }

  override protected def printConclusion(graph: G, console: OutputConsole): Unit = {
    console.writeln(
      "Branches of the spanning tree",
       treeEdges.map(edge =>
         graph.getAnchorPair(edge)
          .get
          .sortedNamePair
        )
    )

    console.writeln();

    //TODO! In writeln(), use Numbers.smartFormat, as well as Set/List/Seq formatting!
    console.writeln(
      "Total weight of the spanning tree",
      treeEdges.totalWeight
    );
  }

  override protected def onInit(graph: G, console: OutputConsole): (G, Boolean) = {
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

  override protected def onMainStep(stepIndex: Int, graph: G, console: OutputConsole): (G, Boolean) = {
    val (nextTreeVertex, nextTreeEdge) =
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

    this.nextTreeVertex =
      nextTreeVertex

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

    conclusionReached =
      fringe.isEmpty

    (graph, !conclusionReached)
  }

  override protected def onConclusion(stepIndex: Int, graph: G, console: OutputConsole): G = {
    treeWeight =
      treeEdges
        .map(_.weight)
        .sum

    graph
  }

  override def partialSolutionVertexes: Set[PrimRuntimeVertex] =
    treeVertexes

  override def partialSolutionLinks: Set[PrimRuntimeEdge] =
    treeEdges


}
