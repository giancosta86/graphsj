package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.Graph
import info.gianlucacosta.eighthbridge.fx.{VisualArc, VisualGraph, VisualVertex}
import info.gianlucacosta.graphsj.sdk.Scenario

//TODO! This could stay in the root of the SDK!
trait DirectedGraphScenario extends Scenario {
  override def createDesignGraph(): VisualGraph =
    Graph.createDirected[VisualVertex, VisualArc]()
}
