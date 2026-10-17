package info.gianlucacosta.graphsj.windows

import info.gianlucacosta.eighthbridge.fx.graph.{VisualArc, VisualGraph, VisualVertex}
import info.gianlucacosta.graphsj.sdk.{Algorithm, Scenario}

package object main {
  type VisualScenario =
    Scenario[VisualVertex, VisualArc, VisualGraph]

  type VisualAlgorithm =
    Algorithm[VisualVertex, VisualArc, VisualGraph]
}
