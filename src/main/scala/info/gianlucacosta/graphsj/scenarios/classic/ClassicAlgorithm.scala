package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.Graph
import info.gianlucacosta.eighthbridge.fx.VisualGraph
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

  protected def createRuntimeGraph(designGraph: DesignG): G =
    designGraph
      .mapWithFacetsTo(
        targetVertexCreator =
          designVertex =>
            createRuntimeVertex(designVertex)
              .copyFacetsFrom(designVertex),
        targetLinkCreator =
          (firstDesignAnchor, secondDesignAnchor, designLink) =>
            createRuntimeLink(firstDesignAnchor, secondDesignAnchor, designLink)
              .copyFacetsFrom(designLink)
      )

  protected def createRuntimeVertex(designVertex: DesignV): V

  protected def createRuntimeLink(firstAnchor: DesignV, secondAnchor: DesignV, designLink: DesignL): L

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
