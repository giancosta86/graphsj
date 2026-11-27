package info.gianlucacosta.graphsj.scenarios.classic

import info.gianlucacosta.eighthbridge.core.Graph
import info.gianlucacosta.eighthbridge.fx.VisualGraph
import info.gianlucacosta.graphsj.scenarios.classic.runtime.{ClassicRuntimeLink, ClassicRuntimeVertex}

sealed trait ClassicAlgorithmStepOutcome[V <: ClassicRuntimeVertex, L <: ClassicRuntimeLink] {
  val graph: Graph[V, L]

  val canProceed: Boolean

  val canGoToNextStep: Boolean

  val conclusionReached: Boolean

  def stepResult: (VisualGraph, Boolean) =
    (graph, canGoToNextStep)
}

case class GoOn[V <: ClassicRuntimeVertex, L <: ClassicRuntimeLink](
   graph: Graph[V, L]
) extends ClassicAlgorithmStepOutcome[V, L] {
  override val canProceed: Boolean =
    true

  override val canGoToNextStep: Boolean =
    true

  override val conclusionReached: Boolean =
    false
}

case class Conclude[V <: ClassicRuntimeVertex, L <: ClassicRuntimeLink](
  graph: Graph[V, L]
) extends ClassicAlgorithmStepOutcome[V, L] {
  override val canProceed: Boolean =
    true

  override val canGoToNextStep: Boolean =
    false

  override val conclusionReached: Boolean =
    true
}

case class Interrupt[V <: ClassicRuntimeVertex, L <: ClassicRuntimeLink](
  graph: Graph[V, L]
) extends ClassicAlgorithmStepOutcome[V, L] {
  override val canProceed: Boolean =
    false

  override val canGoToNextStep: Boolean =
    false

  override val conclusionReached: Boolean =
    false
}