package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.eighthbridge.fx.GraphCanvasController
import info.gianlucacosta.graphsj.scenarios.classic.ClassicScenario
import info.gianlucacosta.graphsj.sdk.Algorithm
import info.gianlucacosta.graphsj.sdk.scenarios.UndirectedGraphScenario


object PrimScenario {
  val Name: String =
    "Prim's Shortest Spanning Tree (SST)"
}

class PrimScenario
  extends ClassicScenario
  with UndirectedGraphScenario {
  override val name: String =
    PrimScenario.Name

  override def createAlgorithm(): Algorithm =
    new PrimAlgorithm(verbose)

  override def createDesignController(): GraphCanvasController =
    new PrimDesignController

  override def createRuntimeController(): GraphCanvasController =
    new PrimRuntimeController
}
