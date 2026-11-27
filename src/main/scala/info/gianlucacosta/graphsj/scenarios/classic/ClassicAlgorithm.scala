package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.Graph
import info.gianlucacosta.eighthbridge.fx.VisualGraph
import info.gianlucacosta.graphsj.scenarios.classic.design.{ClassicDesignLink, ClassicDesignVertex}
import info.gianlucacosta.graphsj.scenarios.classic.runtime.{ClassicRuntimeLink, ClassicRuntimeVertex}
import info.gianlucacosta.graphsj.sdk.ouputconsoles.BufferedOutputConsole
import info.gianlucacosta.graphsj.sdk.{Algorithm, OutputConsole}
import info.gianlucacosta.helios.fx.dialogs.{Alerts, InputDialogs}

abstract class ClassicAlgorithm(protected val verbose: Boolean) extends Algorithm {
  type DesignV <:
    ClassicDesignVertex

  type DesignL <:
    ClassicDesignLink

  final type DesignG =
    Graph[DesignV, DesignL]

  type V <:
    ClassicRuntimeVertex

  type L <:
    ClassicRuntimeLink

  final type G =
    Graph[V, L]

  private var _conclusionReached: Boolean =
    false

  def conclusionReached: Boolean =
    _conclusionReached

  override def runStep(stepIndex: Int, graph: VisualGraph, console: OutputConsole): (VisualGraph, Boolean) = {
    val stepOutcome: ClassicAlgorithmStepOutcome[V, L] =
      stepIndex match {
        case 0 =>
          handleInitStep(graph, console)

        case _ =>
          handleMainStep(stepIndex, graph, console)
      }

    if (stepOutcome.conclusionReached) {
      handleConclusion(stepIndex, graph, console)
    }

    stepOutcome.stepResult
  }

  private def handleInitStep(graph: VisualGraph, console: OutputConsole): ClassicAlgorithmStepOutcome[V, L] = {
    val runtimeGraph: G =
      createRuntimeGraph(graph.asInstanceOf[DesignG])

    checkRequirements(runtimeGraph)

    val initOutcome =
      onInit(runtimeGraph, console)

    if (initOutcome.canProceed && verbose) {
      {
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

      {
        val virtualConsole =
          new BufferedOutputConsole

        printAfterInit(initOutcome.graph, virtualConsole)

        virtualConsole.withNonEmptyText(text => {
          console.writeHeader("Before step 1")
          console.writeln()
          console.write(text)
        })
      }
    }

    initOutcome
  }

  private def handleMainStep(stepIndex: Int, graph: VisualGraph, console: OutputConsole): ClassicAlgorithmStepOutcome[V, L] = {
    if (verbose) {
      console.writeln()
      console.writeHeader("Step " + stepIndex);
      console.writeln();
    }

    val mainStepOutcome =
      onMainStep(stepIndex, graph.asInstanceOf[G], console)

    if (mainStepOutcome.canProceed && verbose) {
      val virtualConsole =
        new BufferedOutputConsole

      printAfterMainStep(stepIndex, mainStepOutcome.graph, virtualConsole)

      virtualConsole.withNonEmptyText(text => {
        console.writeln("At the end of the step:")
        console.writeln()
        console.write(text)
        console.writeln()
        console.writeln()
      })
    }

    mainStepOutcome
  }

  private def handleConclusion(stepIndex: Int, graph: VisualGraph, console: OutputConsole): Unit = {
    _conclusionReached =
      true

    onConclusion(stepIndex, graph.asInstanceOf[G], console)

    val virtualConsole =
      new BufferedOutputConsole

    printConclusion(stepIndex, graph.asInstanceOf[G], virtualConsole)

    virtualConsole.withNonEmptyText(text => {
      console.writeHeader("Conclusion")
      console.writeln()
      console.write(text)
    })
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

  protected def onInit(graph: G, console: OutputConsole): ClassicAlgorithmStepOutcome[V, L]

  protected def onMainStep(stepIndex: Int, graph: G, console: OutputConsole): ClassicAlgorithmStepOutcome[V, L]

  protected def printLegend(console: OutputConsole): Unit = {}

  protected def printAfterInit(graph: G, console: OutputConsole): Unit = {
    printGraphSnapshot(graph, console)
  }

  protected def printAfterMainStep(stepIndex: Int, graph: G, console: OutputConsole): Unit = {
    printGraphSnapshot(graph, console)
  }

  protected def printGraphSnapshot(graph: G, console: OutputConsole): Unit

  protected def onConclusion(stepIndex: Int, graph: G, console: OutputConsole): Unit = {}

  protected def printConclusion(stepIndex: Int, graph: G, console: OutputConsole): Unit = {}

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
