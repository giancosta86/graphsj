package info.gianlucacosta.graphsj.windows

import info.gianlucacosta.eighthbridge.fx.graph.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.graphsj.sdk.{Algorithm, Scenario}

package object main {
  type VisualScenario =
    Scenario[VisualVertex, VisualLink, VisualGraph]

  type VisualAlgorithm =
    Algorithm[VisualVertex, VisualLink, VisualGraph]
}
