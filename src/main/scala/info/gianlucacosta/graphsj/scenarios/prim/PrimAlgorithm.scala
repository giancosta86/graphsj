package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.eighthbridge.core.facets.Weight
import info.gianlucacosta.graphsj.scenarios.classic.{ClassicAlgorithm, ClassicAlgorithmStepOutcome, Conclude, GoOn, Interrupt}
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

  //TODO! Should I use getters, or would that be over-encapsulation?
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

  override protected def createRuntimeLink(firstAnchor: PrimDesignVertex, secondAnchor: PrimDesignVertex, designLink: PrimDesignEdge): PrimRuntimeEdge =
    new PrimRuntimeEdge(this)

  override protected def onInit(graph: G, console: OutputConsole): ClassicAlgorithmStepOutcome[PrimRuntimeVertex, PrimRuntimeEdge] = {
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

        GoOn(graph)
      case None =>
        Interrupt(graph)
    }
  }

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

  protected override def printGraphSnapshot(graph: G, console: OutputConsole): Unit = {
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

    console.writeln()

    super.printGraphSnapshot(graph, console)
  }

  override protected def onMainStep(stepIndex: Int, graph: G, console: OutputConsole): ClassicAlgorithmStepOutcome[PrimRuntimeVertex, PrimRuntimeEdge] = {
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

    if (fringe.isEmpty) {
      Conclude(graph)
    } else {
      GoOn(graph)
    }
  }

  override protected def printAfterMainStep(stepIndex: Int, graph: G, console: OutputConsole): Unit = {
    console.writeln("Vbar", nextTreeVertex.name)

    super.printAfterMainStep(stepIndex, graph, console)
  }

  override protected def onConclusion(stepIndex: Int, graph: G, console: OutputConsole): Unit = {
    treeWeight =
      treeEdges
        .map(_.weight)
        .sum
  }

  override protected def printConclusion(stepIndex: Int, graph: G, console: OutputConsole): Unit = {
    console.writeln(
      "Branches of the spanning tree",
       treeEdges.map(edge =>
         graph.getAnchorPair(edge)
          .get
          .sortedNamePair
        )
    )

    console.writeln();

    console.writeln(
      "Total weight of the spanning tree",
      treeEdges.totalWeight
    );
  }

  override def partialSolutionVertexes: Set[PrimRuntimeVertex] =
    treeVertexes

  override def partialSolutionLinks: Set[PrimRuntimeEdge] =
    treeEdges

  override def conclusionVertexes: Set[PrimRuntimeVertex] =
    treeVertexes

  override def conclusionLinks: Set[PrimRuntimeEdge] =
    treeEdges
}
