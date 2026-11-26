package info.gianlucacosta.graphsj

import info.gianlucacosta.graphsj.windows.main.MainWindowController
import info.gianlucacosta.helios.files.DirectoryWatcher

import java.nio.file.{Path, WatchEvent}
import java.time.Duration


private class ScenariosDirectoryWatcher(
                                         mainWindowController: MainWindowController,
                                         baseDirectory: Path
                                       ) extends DirectoryWatcher(
  baseDirectory,
  Duration.ofSeconds(8)) {

  override def onEvents(events: List[WatchEvent[_]]): Unit =
    mainWindowController.scenarioRepository =
      new ScenarioRepository(baseDirectory.toFile)
}
