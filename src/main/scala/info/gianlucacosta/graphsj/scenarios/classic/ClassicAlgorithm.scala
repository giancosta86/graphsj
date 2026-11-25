package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.directed.{Arc, HasBarbsSettable}
import info.gianlucacosta.eighthbridge.core.facets.{HasLabel, HasLabelSettable, HasName, HasNameSettable, HasSelected, HasSelectedSettable, HasStyleClasses, HasStyleClassesSettable, HasWeightBounded, HasWeightBoundedSettable}
import info.gianlucacosta.eighthbridge.core.{Facet, Graph}
import info.gianlucacosta.eighthbridge.fx.facets.{HasCenter, HasCenterSettable, HasHandles, HasHandlesSettable, HasOptionalCustomLabelCenter, HasOptionalCustomLabelCenterSettable}
import info.gianlucacosta.eighthbridge.fx.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.graphsj.scenarios.classic.design.{ClassicDesignLink, ClassicDesignVertex}
import info.gianlucacosta.graphsj.scenarios.classic.runtime.{ClassicRuntimeLink, ClassicRuntimeVertex}
import info.gianlucacosta.graphsj.sdk.{Algorithm, OutputConsole}
import info.gianlucacosta.helios.fx.dialogs.InputDialogs

abstract class ClassicAlgorithm(protected val verbose: Boolean) extends Algorithm {
  type DesignVertex <:
    ClassicDesignVertex

  type DesignLink <:
    ClassicDesignLink

  type DesignGraph =
    Graph[DesignVertex, DesignLink]

  type RuntimeVertex <:
    ClassicRuntimeVertex

  type RuntimeLink <:
    ClassicRuntimeLink

  type RuntimeGraph =
    Graph[RuntimeVertex, RuntimeLink]

  protected final type V =
    RuntimeVertex

  protected final type L =
    RuntimeLink

  protected final type G =
    RuntimeGraph

  override def runStep(stepIndex: Int, graph: VisualGraph, console: OutputConsole): (VisualGraph, Boolean) = {
    val (nextGraph, shouldGoOn) =
      stepIndex match {
        case 0 =>
          val runtimeGraph: RuntimeGraph =
            createRuntimeGraph(graph.asInstanceOf[DesignGraph])

          checkRequirements(runtimeGraph)

          onInit(runtimeGraph, console)

        case _ =>
          onMainStep(stepIndex, graph.asInstanceOf[RuntimeGraph], console)
      }

    if (shouldGoOn) {
      (nextGraph.asInstanceOf[VisualGraph], true)
    } else {
      val finalGraph: VisualGraph =
        onExpectedEnd(stepIndex, graph.asInstanceOf[RuntimeGraph], console)
          .asInstanceOf[VisualGraph] //TODO! Is there no way to delete this upcast and others?

      (finalGraph, false)
    }
  }

  protected def checkRequirements(graph: RuntimeGraph): Unit = {
    require(
      graph.nonEmpty,
      "The graph cannot be empty!"
    )

    require(
      graph.isolatedVertexes.isEmpty,
      "The graph must not have isolated vertexes!"
    )
  }

  protected def createRuntimeGraph(designGraph: DesignGraph): RuntimeGraph = {
    var runtimeGraph: RuntimeGraph = (
      if (designGraph.isDirected)
        Graph.createDirected()
      else
        Graph.createUndirected()
      ).asInstanceOf[RuntimeGraph]

    var runtimeVertexByDesignVertex: Map[DesignVertex, RuntimeVertex] =
      Map[DesignVertex, RuntimeVertex]()

    designGraph.vertexes.foreach(designVertex => {
      val runtimeVertex =
        copyVertexFacets(
          designVertex,
          createRuntimeVertex(designVertex)
        )

      runtimeVertexByDesignVertex +=
        designVertex -> runtimeVertex

      runtimeGraph =
        runtimeGraph.addVertex(runtimeVertex)
    })

    designGraph.links.foreach(designLink => {
      val (firstDesignAnchor, secondDesignAnchor): (DesignVertex, DesignVertex) =
        designGraph.anchorsByLink(designLink).toPair

      val runtimeAnchors =
        (
          runtimeVertexByDesignVertex(firstDesignAnchor),
          runtimeVertexByDesignVertex(secondDesignAnchor)
        )

      val runtimeLink: RuntimeLink =
        copyLinkFacets(
          designLink,
          createRuntimeLink(designLink)
        )

      runtimeGraph =
        runtimeGraph.addLink(runtimeAnchors, runtimeLink)
    })

    runtimeGraph
  }

  protected def createRuntimeVertex(designVertex: DesignVertex): RuntimeVertex

  protected def copyVertexFacets(designVertex: DesignVertex, runtimeVertex: RuntimeVertex): RuntimeVertex = {
    var result: RuntimeVertex =
      runtimeVertex

    Facet.as[HasCenterSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasCenter](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setCenter(facetedDesignVertex.center).asInstanceOf[RuntimeVertex]
      })
    )

    Facet.as[HasNameSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasName](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setName(facetedDesignVertex.name).asInstanceOf[RuntimeVertex]
      })
    )

    Facet.as[HasLabelSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasLabel](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setLabel(facetedDesignVertex.label).asInstanceOf[RuntimeVertex]
      })
    )

    Facet.as[HasSelectedSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasSelected](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setSelected(facetedDesignVertex.selected).asInstanceOf[RuntimeVertex]
      })
    )

    Facet.as[HasStyleClassesSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasStyleClasses](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setStyleClasses(facetedDesignVertex.styleClasses).asInstanceOf[RuntimeVertex]
      })
    )

    result
  }

  protected def createRuntimeLink(designLink: DesignLink): RuntimeLink

  protected def copyLinkFacets(designLink: DesignLink, runtimeLink: RuntimeLink): RuntimeLink = {
    var result: RuntimeLink =
      runtimeLink

    Facet.as[HasNameSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasName](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setName(facetedDesignLink.name).asInstanceOf[RuntimeLink]
      })
    )

    Facet.as[HasLabelSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasLabel](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setLabel(facetedDesignLink.label).asInstanceOf[RuntimeLink]
      })
    )

    Facet.as[HasSelectedSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasSelected](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setSelected(facetedDesignLink.selected).asInstanceOf[RuntimeLink]
      })
    )

    Facet.as[HasStyleClassesSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasStyleClasses](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setStyleClasses(facetedDesignLink.styleClasses).asInstanceOf[RuntimeLink]
      })
    )

    Facet.as[HasHandlesSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasHandles](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setHandles(facetedDesignLink.handles).asInstanceOf[RuntimeLink]
      })
    )

    Facet.as[HasOptionalCustomLabelCenterSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasOptionalCustomLabelCenter](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setCustomLabelCenter(facetedDesignLink.customLabelCenter).asInstanceOf[RuntimeLink]
      })
    )

    Facet.as[HasBarbsSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[Arc](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setBarbs(facetedDesignLink.barbs).asInstanceOf[RuntimeLink]
      })
    )

    Facet.as[HasWeightBoundedSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasWeightBounded](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setWeight(
            demand=
              facetedDesignLink.demand,
            weight=
              facetedDesignLink.weight,
            capacity=
              facetedDesignLink.capacity
          ).asInstanceOf[RuntimeLink]
      })
    )

    result
  }

  protected def onInit(graph: RuntimeGraph, console: OutputConsole): (RuntimeGraph, Boolean)

  protected def onMainStep(stepIndex: Int, graph: RuntimeGraph, console: OutputConsole): (RuntimeGraph, Boolean)

  protected def onExpectedEnd(stepIndex: Int, graph: RuntimeGraph, console: OutputConsole): RuntimeGraph =
    graph

  def partialSolutionVertexes: Set[RuntimeVertex] =
    Set[RuntimeVertex]()

  def partialSolutionLinks: Set[RuntimeLink] =
    Set[RuntimeLink]()

  def fullSolutionVertexes: Set[RuntimeVertex] =
    Set[RuntimeVertex]()

  def fullSolutionLinks: Set[RuntimeLink] =
    Set[RuntimeLink]()

  protected def askForStartVertex(graph: RuntimeGraph): Option[RuntimeVertex] = {
    val sortedVertexes: List[RuntimeVertex] =
      graph
        .vertexes
        .toList
        .sortBy(_.name)

    InputDialogs.askForItem(
      message=
        "Please, choose the start vertex:",
      items=
        sortedVertexes
    )
  }
}
