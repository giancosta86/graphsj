package info.gianlucacosta.graphsj.windows.main

import info.gianlucacosta.eighthbridge.graphs.point2point.visual.{VisualGraph, VisualLink, VisualVertex}
import info.gianlucacosta.graphsj.Scenario

private case class GraphDocument[V <: VisualVertex[V], L <: VisualLink[L], G <: VisualGraph[V, L, G]]
(scenario: Scenario[V, L, G], designGraph: G)
