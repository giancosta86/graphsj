package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.directed.{Arc, HasBarbsSettable}
import info.gianlucacosta.eighthbridge.core.facets.{HasLabel, HasLabelSettable, HasName, HasNameSettable, HasSelected, HasSelectedSettable, HasStyleClasses, HasStyleClassesSettable, HasWeightBounded, HasWeightBoundedSettable}
import info.gianlucacosta.eighthbridge.core.{Facet, Graph}
import info.gianlucacosta.eighthbridge.fx.facets.{HasCenter, HasCenterSettable, HasHandles, HasHandlesSettable, HasOptionalCustomLabelCenter, HasOptionalCustomLabelCenterSettable}
import info.gianlucacosta.eighthbridge.fx.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.graphsj.scenarios.classic.design.{ClassicDesignLink, ClassicDesignVertex}
import info.gianlucacosta.graphsj.scenarios.classic.runtime.{ClassicRuntimeLink, ClassicRuntimeVertex}
import info.gianlucacosta.graphsj.sdk.ouputconsoles.BufferedOutputConsole
import info.gianlucacosta.graphsj.sdk.{Algorithm, OutputConsole}
import info.gianlucacosta.helios.fx.dialogs.InputDialogs

abstract class ClassicAlgorithm(protected val verbose: Boolean) extends Algorithm {
  type DesignV <:
    ClassicDesignVertex

  type DesignL <:
    ClassicDesignLink

  type DesignG =
    Graph[DesignV, DesignL]

  type V <:
    ClassicRuntimeVertex

  type L <:
    ClassicRuntimeLink

  type G =
    Graph[V, L]

  var conclusionReached =
    false

  override def runStep(stepIndex: Int, graph: VisualGraph, console: OutputConsole): (VisualGraph, Boolean) = {
    val (nextGraph, shouldGoOn) =
      stepIndex match {
        case 0 =>
          val runtimeGraph: G =
            createRuntimeGraph(graph.asInstanceOf[DesignG])

          checkRequirements(runtimeGraph)

          if (verbose) {
            val virtualConsole =
              new BufferedOutputConsole

            printLegend(virtualConsole)

            virtualConsole.withNonEmptyText(text => {
              console.writeHeader("Legend")
              console.writeln()
              console.write(text)
              console.writeln()
            })
          }

          val (postInitGraph, shouldGoOnAfterInit) =
            onInit(runtimeGraph, console)

          if (shouldGoOnAfterInit && verbose) {
            val virtualConsole =
              new BufferedOutputConsole

            printAfterInit(postInitGraph, virtualConsole)

            virtualConsole.withNonEmptyText(text => {
              console.writeHeader("Before step 1")
              console.write(text)
            })
          }

          (postInitGraph, shouldGoOnAfterInit)
        case _ =>
          if (verbose) {
            console.writeHeader("Step " + stepIndex);
            console.writeln();
          }

          val (postStepGraph, shouldGoOn) =
            onMainStep(stepIndex, graph.asInstanceOf[G], console)

          if (verbose) {
            val virtualConsole =
              new BufferedOutputConsole

            printAfterMainStep(stepIndex, postStepGraph, virtualConsole)

            virtualConsole.withNonEmptyText(text => {
              console.writeln("At the end of the step:");
              console.writeln();
              console.write(text)
              console.writeln();
              console.writeln();
            })
          }

          (postStepGraph, shouldGoOn)
      }

    if (shouldGoOn) {
      (nextGraph.asInstanceOf[VisualGraph], true)
    } else {
      if (conclusionReached) {
        val conclusionGraph =
          onConclusion(stepIndex, graph.asInstanceOf[G], console)

        val virtualConsole =
          new BufferedOutputConsole

        printConclusion(conclusionGraph, virtualConsole)

        virtualConsole.withNonEmptyText(text => {
          console.writeHeader("Conclusion")
          console.writeln()
          console.write(text)
          console.writeln()
        })

        (conclusionGraph, false)
      } else {
        (graph, false)
      }
    }
  }

  protected def checkRequirements(graph: G): Unit = {
    require(
      graph.nonEmpty,
      "The graph cannot be empty!"
    )

    require(
      graph.isolatedVertexes.isEmpty,
      "The graph must not have isolated vertexes!"
    )
  }

  protected def printLegend(console: OutputConsole): Unit = {}

  protected def printAfterInit(graph: G, console: OutputConsole): Unit = {}

  //TODO! Also print all the vertex labels?
  protected def printAfterMainStep(stepIndex: Int, graph: G, console: OutputConsole): Unit = {}

  protected def printConclusion(graph: G, console: OutputConsole): Unit = {}

  protected def createRuntimeGraph(designGraph: DesignG): G = {
    var runtimeGraph: G = (
      if (designGraph.isDirected)
        Graph.createDirected()
      else
        Graph.createUndirected()
      ).asInstanceOf[G]

    var runtimeVertexByDesignVertex: Map[DesignV, V] =
      Map[DesignV, V]()

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
      val (firstDesignAnchor, secondDesignAnchor): (DesignV, DesignV) =
        designGraph.anchorsByLink(designLink).toPair

      val runtimeAnchors =
        (
          runtimeVertexByDesignVertex(firstDesignAnchor),
          runtimeVertexByDesignVertex(secondDesignAnchor)
        )

      val runtimeLink: L =
        copyLinkFacets(
          designLink,
          createRuntimeLink(designLink)
        )

      runtimeGraph =
        runtimeGraph.addLink(runtimeAnchors, runtimeLink)
    })

    runtimeGraph
  }

  protected def createRuntimeVertex(designVertex: DesignV): V

  protected def copyVertexFacets(designVertex: DesignV, runtimeVertex: V): V = {
    var result: V =
      runtimeVertex

    Facet.as[HasCenterSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasCenter](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setCenter(facetedDesignVertex.center).asInstanceOf[V]
      })
    )

    Facet.as[HasNameSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasName](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setName(facetedDesignVertex.name).asInstanceOf[V]
      })
    )

    Facet.as[HasLabelSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasLabel](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setLabel(facetedDesignVertex.label).asInstanceOf[V]
      })
    )

    Facet.as[HasSelectedSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasSelected](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setSelected(facetedDesignVertex.selected).asInstanceOf[V]
      })
    )

    Facet.as[HasStyleClassesSettable](result).foreach(facetedRuntimeVertex =>
      Facet.as[HasStyleClasses](designVertex).foreach(facetedDesignVertex => {
        result =
          facetedRuntimeVertex.setStyleClasses(facetedDesignVertex.styleClasses).asInstanceOf[V]
      })
    )

    result
  }

  protected def createRuntimeLink(designLink: DesignL): L

  protected def copyLinkFacets(designLink: DesignL, runtimeLink: L): L = {
    var result: L =
      runtimeLink

    Facet.as[HasNameSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasName](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setName(facetedDesignLink.name).asInstanceOf[L]
      })
    )

    Facet.as[HasLabelSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasLabel](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setLabel(facetedDesignLink.label).asInstanceOf[L]
      })
    )

    Facet.as[HasSelectedSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasSelected](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setSelected(facetedDesignLink.selected).asInstanceOf[L]
      })
    )

    Facet.as[HasStyleClassesSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasStyleClasses](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setStyleClasses(facetedDesignLink.styleClasses).asInstanceOf[L]
      })
    )

    Facet.as[HasHandlesSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasHandles](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setHandles(facetedDesignLink.handles).asInstanceOf[L]
      })
    )

    Facet.as[HasOptionalCustomLabelCenterSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[HasOptionalCustomLabelCenter](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setCustomLabelCenter(facetedDesignLink.customLabelCenter).asInstanceOf[L]
      })
    )

    Facet.as[HasBarbsSettable](result).foreach(facetedRuntimeLink =>
      Facet.as[Arc](designLink).foreach(facetedDesignLink => {
        result =
          facetedRuntimeLink.setBarbs(facetedDesignLink.barbs).asInstanceOf[L]
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
          ).asInstanceOf[L]
      })
    )

    result
  }

  protected def onInit(graph: G, console: OutputConsole): (G, Boolean)

  protected def onMainStep(stepIndex: Int, graph: G, console: OutputConsole): (G, Boolean)

  protected def onConclusion(stepIndex: Int, graph: G, console: OutputConsole): G =
    graph

  def partialSolutionVertexes: Set[V] =
    Set[V]()

  def partialSolutionLinks: Set[L] =
    Set[L]()

  def conclusionVertexes: Set[V] =
    Set[V]()

  def conclusionLinks: Set[L] =
    Set[L]()

  protected def askForStartVertex(graph: G): Option[V] = {
    val sortedVertexes: List[V] =
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
