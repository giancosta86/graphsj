package info.gianlucacosta.graphsj.scenarios.prim

import info.gianlucacosta.graphsj.sdk.ScenarioFactory

class PrimScenarioFactory extends ScenarioFactory {
  override def scenarioName: String =
    PrimScenario.Name

  override def createScenario: Option[PrimScenario] =
    Some(new PrimScenario)
}
