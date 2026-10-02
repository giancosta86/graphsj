package info.gianlucacosta.graphsj

import java.io.File
import java.net.URL
import javafx.fxml.FXMLLoader
import javafx.stage.Stage

import info.gianlucacosta.graphsj.icons.MainIcon
import info.gianlucacosta.graphsj.windows.main.MainWindowController
import info.gianlucacosta.helios.apps.{AppInfo, AuroraAppInfo}
import info.gianlucacosta.helios.desktop.DesktopUtils
import info.gianlucacosta.helios.fx.application.{AppBase, AppMain, SplashStage}

import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane


object App extends AppMain[App](classOf[App]) {
  //TODO! Hardwire the current version here!
  val ScenariosApiUrl =
    new URL("https://api.github.com/repos/giancosta86/graphsJ-scenarios/releases/latest")

  private val MajorVersion =
    ArtifactInfo.version.split('.').head


  val DefaultExtension =
    s".gj${MajorVersion}"


  val ScenariosDirectory = new File(
    DesktopUtils.homeDirectory.get,
    s".${ArtifactInfo.name}${MajorVersion}"
  )

  def ensureScenariosDirectory(): Unit = {
    require(
      ScenariosDirectory.isDirectory ||
        ScenariosDirectory.mkdirs(),

      "Cannot create the scenarios directory:\n'${ScenariosDirectory}'"
    )
  }
}


class App extends AppBase(AuroraAppInfo(ArtifactInfo, MainIcon)) {
  override def startup(appInfo: AppInfo, splashStage: SplashStage, primaryStage: Stage): Unit = {
    App.ensureScenariosDirectory()

    val loader =
      new FXMLLoader(
        classOf[MainWindowController[_, _, _]].getResource("MainWindow.fxml")
      )


    val rootComponent: javafx.scene.layout.BorderPane =
      loader.load()


    val mainWindowController =
      loader.getController.asInstanceOf[MainWindowController[_, _, _]]


    val scenarioRepository =
      new ScenarioRepository(App.ScenariosDirectory)


    mainWindowController.setup(primaryStage, appInfo, scenarioRepository)


    new ScenariosDirectoryWatcher(mainWindowController, App.ScenariosDirectory.toPath) {
      start()
    }


    val scene = new Scene {
      root =
        new BorderPane(rootComponent)
    }


    Platform.runLater {
      primaryStage.setMaximized(true)

      primaryStage.setScene(scene)
    }
  }
}
