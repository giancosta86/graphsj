package info.gianlucacosta.graphsj.windows.main

private trait RunState

private case object NotRunning extends RunState

private case object InFullRun extends RunState

private case object InStepRun extends RunState

private case object Finished extends RunState
