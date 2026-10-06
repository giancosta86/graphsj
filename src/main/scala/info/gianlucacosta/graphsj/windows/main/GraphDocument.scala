package info.gianlucacosta.graphsj.windows.main

import info.gianlucacosta.eighthbridge.fx.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.graphsj.Scenario

private case class GraphDocument[V <: VisualVertex, L <: VisualLink, G <: VisualGraph[V, L]]
(scenario: Scenario[V, L, G], designGraph: G)
